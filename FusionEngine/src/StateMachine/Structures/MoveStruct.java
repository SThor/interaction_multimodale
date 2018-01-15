/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StateMachine.Structures;

import java.awt.Point;
import java.util.List;

/**
 *
 * @author givelpa
 */
public class MoveStruct implements TestableStruct{
    List<Shape> candidates;
    Point position;

    @Override
    public boolean structComplete() {
        return !candidates.isEmpty() && position!=null;
    }
    
}
