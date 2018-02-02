/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stateMachine;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;
import java.awt.Color;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import stateMachine.structures.TestableStruct;

/**
 *
 * @author givelpa
 */
public class CreateStruct implements TestableStruct{
    Shape.Type type = Shape.Type.RECTANGLE;
    Point position = new Point(0,0);
    String color = "BLACK";

    public CreateStruct(Shape.Type type) {
        this.type = type;
    }

    @Override
    public boolean structComplete() {
        return true;
    }

    @Override
    public void execute(Ivy bus) {
        if(type.equals(Shape.Type.RECTANGLE)){
            try {
                bus.sendMsg("Palette:CreerRectangle x=" + this.position.x + ""
                        + " y=" + this.position.y + " couleurFond=" + this.color);
                System.out.println("Palette:CreerRectangle x=" + this.position.x + ""
                        + " y=" + this.position.y + " couleurFond=" + this.color);
            } catch (IvyException ex) {
                Logger.getLogger(CreateStruct.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (type.equals(Shape.Type.ELLIPSE)){
            try {
                bus.sendMsg("Palette:CreerEllipse x=" + this.position.x + ""
                        + " y=" + this.position.y + " couleurFond=" + this.color);
                System.out.println("Palette:CreerEllipse x=" + this.position.x + ""
                        + " y=" + this.position.y + " couleurFond=" + this.color);
            } catch (IvyException ex) {
                Logger.getLogger(CreateStruct.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setColor(String color) {
        switch(color){
            case "vert":
                this.color = "GREEN";
                break;
            case "bleu":
                this.color = "BLUE";
                break;
            case "rouge":
                this.color = "RED";
                break;
            case "orange":
                this.color = "ORANGE";
                break;
            case "jaune":
                this.color = "YELLOW";
                break;
            case "violet":
                this.color = "MAGENTA";
                break;
            case "gris":
                this.color = "GRAY";
                break;
            case "noir":
                this.color = "BLACK";
                break;
            default:
                this.color = color;
                break;
        }
    }
}
