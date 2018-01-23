/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stateMachine;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;
import language.Gesture;
import language.Keyword;
import stateMachine.structures.CreateStruct;
import stateMachine.structures.MoveStruct;
import stateMachine.structures.RemoveStruct;
import stateMachine.structures.Shape;
import stateMachine.structures.TestableStruct;

import java.awt.Point;
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
    
    private Timer actionTimedOut = new Timer(500, (e) -> {
        timer();
    });
    private Timer timerPointing = new Timer(2000, (e) -> {
        timer();
    });

    private Ivy bus;
    
    public enum State{
        WAITING_FOR_GESTURE,
        ACTION,
        INT_1,
        INT_2
    }
    
    private State state;

    public StateMachine() {
        state = State.WAITING_FOR_GESTURE;
        
        setupIvy();
    }
    
    private void setupIvy() {
        bus = new Ivy("Fusion", "Fusion Engine ready", null);
        try {
            bus.start("127.0.0.1:1234");
            bus.sendToSelf(false);
            bus.bindMsg("Palette:Mouse(.*) x=(.*) y=(.*)", (client, args) -> {
                if(args[0].equals("Clicked")){
                    pointing(new Point(Integer.parseInt(args[1]), Integer.parseInt(args[2])));
                }
            });
            bus.bindMsg("Action:couleur Couleur:(.*)", (client, args) -> {
                SRAColor(args[0]);
            });
            bus.bindMsg("Action:position", (client, args) -> {
                SRA(Keyword.POSITION);
            });
            bus.bindMsg("Action:designer une couleur", (client, args) -> {
                SRA(Keyword.COLOR);
            });
            bus.bindMsg("Action:designer une forme Forme:(.*)", (client, args) -> {
                if(args[0].equals("ce rectangle")){
                    SRA(Keyword.RECTANGLE);
                } else if (args[0].equals("cette ellipse")){
                    SRA(Keyword.ELLIPSE);
                } else {
                    SRA(Keyword.SHAPE);
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
        state = State.ACTION;
    }
    
    public void pointing(Point coords){
        switch(state){
            case WAITING_FOR_GESTURE: //FORBIDDEN
                break;
            case ACTION:
                tmpPoint = coords;
                timerPointing.start();
                actionTimedOut.stop();
                state = State.INT_2;
                break;
            case INT_1:
                tmpPoint = coords;
                updateStructure();
                timerPointing.stop();
                actionTimedOut.start();
                state = State.ACTION;
                break;
            case INT_2: //FORBIDDEN
                break;
        }
    }
    
    public void SRA(Keyword keyword){
        switch(state){
            case WAITING_FOR_GESTURE: //FORBIDDEN
                break;
            case ACTION:
                tmpKeyword = keyword;
                timerPointing.start();
                actionTimedOut.stop();
                state = State.INT_1;
                break;
            case INT_1: //FORBIDDEN
                break;
            case INT_2:
                tmpKeyword = keyword;
                updateStructure();
                timerPointing.stop();
                actionTimedOut.start();
                state = State.ACTION;
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
                state = State.ACTION;
                break;
            case INT_1: //FORBIDDEN
                break;
            case INT_2: //FORBIDDEN
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
                state = State.WAITING_FOR_GESTURE;
                break;
            case INT_1:
                //do nothing, cancel
                timerPointing.stop();
                actionTimedOut.start();
                state = State.ACTION;
                break;
            case INT_2:
                //do nothing, cancel
                timerPointing.stop();
                actionTimedOut.start();
                state = State.ACTION;
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
