package pure.fsm.core.template;

import pure.fsm.core.Transition;
import pure.fsm.core.StateMachine;

public interface StateMachineCallback {

    Transition doWith(Transition transition, StateMachine stateMachine);

    void onLockFailed(Exception e);

    Transition onError(Transition transition, StateMachine stateMachine, Exception e);
}
