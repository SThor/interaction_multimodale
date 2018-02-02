/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stateMachine;

import fr.dgac.ivy.Ivy;
import java.awt.Point;

/**
 *
 * @author givelpa
 */
public interface TestableStruct {
    boolean structComplete();

    public void execute(Ivy bus);
}
