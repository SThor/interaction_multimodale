/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StateMachine.Structures;

import java.util.List;

/**
 *
 * @author givelpa
 */
public class RemoveStruct implements TestableStruct{
    List<Shape> candidates;

    @Override
    public boolean structComplete() {
        return !candidates.isEmpty();
    }
}
