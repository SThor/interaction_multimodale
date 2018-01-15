/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StateMachine.Structures;

import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author givelpa
 */
public class Shape {
    public enum Type{
        RECTANGLE, ELLIPSE
    }
    
    public String name;
    public Type type;
    public Point coords;
    public int lenght, width;
    public Color background, border;
}
