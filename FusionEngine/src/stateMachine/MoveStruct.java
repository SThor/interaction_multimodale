/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stateMachine;


import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import stateMachine.TestableStruct;

/**
 *
 * @author givelpa
 */
public class MoveStruct implements TestableStruct{
    List<Shape> candidates = new ArrayList<>();
    Point position;

    @Override
    public boolean structComplete() {
        return candidates != null && !candidates.isEmpty() && position!=null;
    }

    @Override
    public void execute(Ivy bus) {
        try {
            bus.sendMsg("Palette:DeplacerObjetAbsolu nom=" + this.candidates.get(0).name +" x="+position.x+" y="+position.y);
            System.out.println("Palette:DeplacerObjetAbsolu nom=" + this.candidates.get(0).name);
        } catch (IvyException ex) {
            Logger.getLogger(MoveStruct.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void filter(Point target, Ivy bus) {
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA DANS LE FILTER DE POINT");
        try {
            bus.bindMsg("Palette:ResultatTesterPoint x=" + target.x + " y=" + target.y + " nom=(.*)", (client, args) -> {
                try {
                    bus.sendMsg("Palette:DemanderInfo nom=" + args[0]);
                } catch (IvyException ex) {
                    Logger.getLogger(MoveStruct.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            bus.bindMsg("Palette:Info nom=(.*) x=(.*) y=(.*) longueur=(.*) "
                    + "hauteur=(.*) couleurFond=(.*) couleurContour=(.*)", (client, args) -> {
                Shape shape = new Shape(args[0]);
                shape.coords.x = Integer.parseInt(args[1]);
                shape.coords.y = Integer.parseInt(args[2]);
                shape.width = Integer.parseInt(args[3]);
                shape.height = Integer.parseInt(args[4]);
                shape.background = args[5];
                shape.border = args[6];
                if(args[0].startsWith("R")){
                    shape.type = Shape.Type.RECTANGLE;
                }else if(args[0].startsWith("E")){
                    shape.type = Shape.Type.ELLIPSE;
                }
                candidates.add(shape);
                System.out.println("candidates.size() = " + candidates.size());
            });
            bus.sendMsg("Palette:TesterPoint x=" + target.x + " y=" + target.y);
        } catch (IvyException ex) {
            Logger.getLogger(MoveStruct.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void filter(Shape.Type type) {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA DANS LE FILTER DE SHAPE");
        System.out.println("candidates.size() = " + candidates.size());
        Iterator<Shape> it = candidates.iterator();
        while(it.hasNext()){
            Shape shape = it.next();
            System.out.println("shape.type = " + shape.type);
            System.out.println("type = " + type);
            if(shape.type != type){
                System.out.println("    Je supprime");
                it.remove();
            }
        }
    }

    public void filter(String color) {
        Iterator<Shape> it = candidates.iterator();
        while(it.hasNext()){
            Shape shape = it.next();
            if(shape.background != color){
                it.remove();
            }
        }
    }
}
