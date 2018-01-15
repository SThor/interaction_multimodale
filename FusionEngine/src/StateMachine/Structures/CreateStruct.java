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
public class CreateStruct {
    Shape.Type type;
    Point position;
    Color color;

    public CreateStruct(Shape.Type type) {
        this.type = type;
    }
}
