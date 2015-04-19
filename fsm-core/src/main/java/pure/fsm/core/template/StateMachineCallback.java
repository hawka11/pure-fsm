package pure.fsm.core.template;

import pure.fsm.core.Transition;
import pure.fsm.core.StateMachine;

public interface StateMachineCallback {

    Transition doWith(Transition prevTransition, StateMachine stateMachine);

    void onLockFailed(Exception e);

    Transition onError(Transition prevTransition, StateMachine stateMachine, Exception e);
}
