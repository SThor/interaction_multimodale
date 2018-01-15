/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StateMachine;

import Language.Gesture;
import StateMachine.Structures.CreateStruct;
import StateMachine.Structures.MoveStruct;
import StateMachine.Structures.RemoveStruct;
import StateMachine.Structures.Shape;

/**
 *
 * @author givelpa
 */
public class StateMachine {

    private MoveStruct structMove;
    private RemoveStruct structRemove;
    private CreateStruct structCreate;
    public enum State{
        WAITING_FOR_GESTURE, CREATE, MOVE, REMOVE, REMOVE_SHAPE_DESIGNATION, MOVE_SHAPE_DESIGNATION
    }
    
    private State state;

    public StateMachine() {
        state = State.WAITING_FOR_GESTURE;
    }
    
    public void gesture(Gesture gesture){
        switch(gesture){
            case Move:
                state = State.MOVE;
                structMove = new MoveStruct();
                break;
            case Remove:
                state = State.REMOVE;
                structRemove = new RemoveStruct();
                break;
            case Ellipse:
                state = State.CREATE;
                structCreate = new CreateStruct(Shape.Type.ELLIPSE);
                break;
            case Rectangle:
                state = State.CREATE;
                structCreate = new CreateStruct(Shape.Type.RECTANGLE);
                break;
        }
    }
    
    //TODO : other events
}
