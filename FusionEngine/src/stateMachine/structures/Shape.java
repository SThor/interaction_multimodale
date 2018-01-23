/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stateMachine.structures;

import java.awt.Color;
import java.awt.Point;

/**
 *
 * @author givelpa
 */
public class Shape {
    public enum Type{
        RECTANGLE, ELLIPSE, UNSPECIFIED
    }

    public Shape(String name) {
        this.name = name;
    }
    
    public String name;
    public Type type = Type.UNSPECIFIED;
    public Point coords;
    public int height, width;
    public String background, border;
}
