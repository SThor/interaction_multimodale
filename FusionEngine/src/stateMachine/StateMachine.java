/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stateMachine;

import stateMachine.structures.TestableStruct;
import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;
import language.Gesture;
import language.Keyword;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author givelpa
 */
public class StateMachine{

    private TestableStruct struct;
    private Point tmpPoint;
    private String tmpColor;
    private Keyword tmpKeyword;
    
    private Timer actionTimedOut = new Timer(3000, (e) -> {
        System.out.println("timer Action");
        timer();
    });
    private Timer timerPointing = new Timer(3000, (e) -> {
        System.out.println("timer Pointing");
        timer();
    });

    private Ivy bus;
    private final double SEUIL_SRA = 0.75;
    private Point oldTmpPoint;

    private void resetValues() {
        tmpPoint = null;
        tmpColor = null;
        tmpKeyword = null;
        struct = null;
    }

    private void getColorFromPosition(Point coords) {
        System.out.println("appel de la fonction relou ************************************************");
        try {
            bus.bindMsg("Palette:ResultatTesterPoint x="+coords.x+" y="+coords.y+" nom=(.*)", (client, args) -> {
                try {
                    bus.bindMsg("Palette:Info nom="+args[0]+" x=(.*) y=(.*) longueur=(.*) hauteur=(.*) couleurFond=(.*) couleurContour=(.*)", (client1, args1) -> {
                        switch(args1[4]){
                            case "green":
                                tmpColor = "GREEN";
                                break;
                            case "blue":
                                tmpColor = "BLUE";
                                break;
                            case "red":
                                tmpColor = "RED";
                                break;
                            case "orange":
                                tmpColor = "ORANGE";
                                break;
                            case "yellow":
                                tmpColor = "YELLOW";
                                break;
                            case "magenta":
                                tmpColor = "MAGENTA";
                                break;
                            case "gray":
                                tmpColor = "GRAY";
                                break;
                            case "black":
                                tmpColor = "BLACK";
                                break;
                        }
                        updateStructure();
                    });
                    bus.sendMsg("Palette:DemanderInfo nom="+args[0]);
                    System.out.println("sent demanderInfo over "+args[0]);
                } catch (IvyException ex) {
                    Logger.getLogger(StateMachine.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            bus.sendMsg("Palette:TesterPoint x="+coords.x+" y="+coords.y);
            System.out.println("Palette:TesterPoint x="+coords.x+" y="+coords.y);
        } catch (IvyException ex) {
            Logger.getLogger(StateMachine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public enum State{
        WAITING_FOR_GESTURE,
        ACTION,
        SRA_SHAPE_THEN_POINTING,
        POINTING_THEN_SRA,
        SRA_COLOR_THEN_POINTING
    }
    
    private State state;

    public void setState(State state) {
        State oldState = this.state;
        this.state = state;
        System.out.println("switched from "+oldState+" to "+state);
    }

    public StateMachine() {
        setState(State.WAITING_FOR_GESTURE);
        
        setupIvy();
    }
    
    private void setupIvy() {
        bus = new Ivy("Fusion", "Fusion Engine ready", null);
        try {
            bus.start("127.255.255.255:2010");
            bus.sendToSelf(false);
            bus.bindMsg("Gesture nom=(.*)", (client, args) -> {
                switch(args[0]){
                    case "Remove":
                        gesture(Gesture.Remove);
                        break;
                    case "Move":
                        gesture(Gesture.Move);
                        break;
                    case "Ellipse":
                        gesture(Gesture.Ellipse);
                        break;
                    case "Rectangle":
                        gesture(Gesture.Rectangle);
                        break;
                }
            });
            bus.bindMsg("Palette:Mouse(.*) x=(.*) y=(.*)", (client, args) -> {
                if(args[0].equals("Clicked")){
                    pointing(new Point(Integer.parseInt(args[1]), Integer.parseInt(args[2])));
                }
            });
            bus.bindMsg("sra5 Parsed=Action:couleur Couleur:(.*) Confidence=(.*) NP(.*)", (client, args) -> {
                if( Double.parseDouble(args[1].replace(',','.'))>SEUIL_SRA){
                    SRAColor(args[0]);
                }
            });
            bus.bindMsg("sra5 Parsed=Action:position Confidence=(.*) NP(.*)", (client, args) -> {
                if( Double.parseDouble(args[0].replace(',','.'))>SEUIL_SRA){
                    SRA(Keyword.POSITION);
                }
            });
            bus.bindMsg("sra5 Parsed=Action:designer une couleur Confidence=(.*) NP(.*)", (client, args) -> {
                if( Double.parseDouble(args[0].replace(',','.'))>SEUIL_SRA){
                    SRA(Keyword.COLOR);
                }
            });
            bus.bindMsg("sra5 Parsed=Action:designer une forme Forme:(.*) Confidence=(.*) NP(.*)", (client, args) -> {
                if( Double.parseDouble(args[1].replace(',','.'))>SEUIL_SRA){
                    if(args[0].equals("ce rectangle")){
                        SRA(Keyword.RECTANGLE);
                    } else if (args[0].equals("cette ellipse")){
                        SRA(Keyword.ELLIPSE);
                    } else {
                        SRA(Keyword.SHAPE);
                    }
                    System.out.println("Designer forme : "+args[0]);
                }
            });
        } catch (IvyException ex) {
            System.out.println("start");
            ex.printStackTrace();
        }
    }
    
    /** cas particulier d'event : quel que soit l'état courant, on va dans vers le même état
     * de plus, factorisation des différents event de gestes en un seul.
     * @param gesture geste reconnu
     */
    public void gesture(Gesture gesture){
        resetValues();
        switch(gesture){
            case Move:
                struct = new MoveStruct();
                break;
            case Remove:
                struct = new RemoveStruct();
                break;
            case Ellipse:
                struct = new CreateStruct(Shape.Type.ELLIPSE);
                break;
            case Rectangle:
                struct = new CreateStruct(Shape.Type.RECTANGLE);
                break;
        }        
        timerPointing.stop();
        actionTimedOut.start();
        setState(State.ACTION);
    }
    
    public void pointing(Point coords){
        switch(state){
            case WAITING_FOR_GESTURE: //FORBIDDEN
                break;
            case ACTION:
                oldTmpPoint = tmpPoint;
                tmpPoint = coords;
                timerPointing.start();
                actionTimedOut.stop();
                setState(State.POINTING_THEN_SRA);
                break;
            case SRA_SHAPE_THEN_POINTING:
                tmpPoint = coords;
                updateStructure();
                timerPointing.stop();
                actionTimedOut.start();
                setState(State.ACTION);
                break;
            case POINTING_THEN_SRA: //FORBIDDEN
                break;
            case SRA_COLOR_THEN_POINTING:
                getColorFromPosition(coords);
                timerPointing.stop();
                actionTimedOut.start();
                setState(State.ACTION);
                break;
        }
    }
    
    public void SRA(Keyword keyword){
        switch(state){
            case WAITING_FOR_GESTURE: //FORBIDDEN
                break;
            case ACTION:
                if(keyword == keyword.POSITION){
                    tmpKeyword = keyword;
                    timerPointing.start();
                    actionTimedOut.stop();
                    setState(State.SRA_SHAPE_THEN_POINTING);
                }else if(keyword == keyword.COLOR){
                    tmpKeyword = keyword;
                    timerPointing.start();
                    actionTimedOut.stop();
                    setState(State.SRA_COLOR_THEN_POINTING);
                }else{
                    tmpKeyword = keyword;
                    timerPointing.start();
                    actionTimedOut.stop();
                    setState(State.SRA_SHAPE_THEN_POINTING);
                }
                break;
            case SRA_SHAPE_THEN_POINTING: //FORBIDDEN
                break;
            case POINTING_THEN_SRA:
                tmpKeyword = keyword;
                if(keyword == Keyword.COLOR){
                    getColorFromPosition(tmpPoint);
                    tmpPoint = oldTmpPoint;
                }
                updateStructure();
                timerPointing.stop();
                actionTimedOut.start();
                setState(State.ACTION);
                break;
            case SRA_COLOR_THEN_POINTING: //FORBIDDEN
                break;
        }
    }
    
    public void SRAColor(String color){
        switch(state){
            case WAITING_FOR_GESTURE: //FORBIDDEN
                break;
            case ACTION:
                tmpColor = color;
                updateStructure();
                timerPointing.stop();
                actionTimedOut.start();
                setState(State.ACTION);
                break;
            case SRA_SHAPE_THEN_POINTING: //FORBIDDEN
                break;
            case POINTING_THEN_SRA: //FORBIDDEN
                break;
            case SRA_COLOR_THEN_POINTING: //FORBIDDEN
                break;
        }
    }
    
    public void timer(){
        switch(state){
            case WAITING_FOR_GESTURE:  //FORBIDDEN
                break;
            case ACTION:
                if(struct.structComplete()){
                    struct.execute(bus);
                }else{
                    //do nothing, cancel
                }
                setState(State.WAITING_FOR_GESTURE);
                actionTimedOut.stop();
                resetValues();
                break;
            case SRA_SHAPE_THEN_POINTING:
                //do nothing, cancel
                timerPointing.stop();
                actionTimedOut.start();
                setState(State.ACTION);
                break;
            case POINTING_THEN_SRA:
                //do nothing, cancel
                timerPointing.stop();
                actionTimedOut.start();
                setState(State.ACTION);
                break;
            case SRA_COLOR_THEN_POINTING:
                //do nothing, cancel
                timerPointing.stop();
                actionTimedOut.start();
                setState(State.ACTION);
                break;
        }
    }

    private void updateStructure() {
        if(struct instanceof CreateStruct){
            if(tmpPoint != null){
                ((CreateStruct)struct).setPosition(tmpPoint);                
            }
            if(tmpColor != null){
                ((CreateStruct)struct).setColor(tmpColor);
            }
            //todo designerCouleur
        }else if(struct instanceof MoveStruct){
            if(tmpPoint != null && tmpKeyword !=null){
                if(tmpKeyword == Keyword.POSITION){
                    ((MoveStruct)struct).setPosition(tmpPoint);
                }else if(tmpKeyword == Keyword.ELLIPSE){
                    ((MoveStruct)struct).filter(tmpPoint, bus);
                    ((MoveStruct)struct).filter(Shape.Type.ELLIPSE);
                }else if(tmpKeyword == Keyword.RECTANGLE){
                    ((MoveStruct)struct).filter(tmpPoint, bus);
                    ((MoveStruct)struct).filter(Shape.Type.RECTANGLE);
                }else if(tmpKeyword == Keyword.SHAPE){
                    ((MoveStruct)struct).filter(tmpPoint, bus);
                }
            }
            if(tmpColor != null){
                ((MoveStruct)struct).filter(tmpColor);
            }
        }else if(struct instanceof RemoveStruct){
            if(tmpPoint != null && tmpKeyword !=null){
                if(tmpKeyword == Keyword.ELLIPSE){
                    ((RemoveStruct)struct).filter(tmpPoint, bus);
                    ((RemoveStruct)struct).filter(Shape.Type.ELLIPSE);
                }else if(tmpKeyword == Keyword.RECTANGLE){
                    ((RemoveStruct)struct).filter(tmpPoint, bus);
                    ((RemoveStruct)struct).filter(Shape.Type.RECTANGLE);
                }else if(tmpKeyword == Keyword.SHAPE){
                    ((RemoveStruct)struct).filter(tmpPoint, bus);
                }
            }
            if(tmpColor != null){
                ((RemoveStruct)struct).filter(tmpColor);
            }
        }
    }
}
