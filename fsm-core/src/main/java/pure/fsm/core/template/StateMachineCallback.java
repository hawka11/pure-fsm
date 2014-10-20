package pure.fsm.core.template;

import pure.fsm.core.StateMachine;

public interface StateMachineCallback {

    StateMachine doWith(StateMachine stateMachine);

    void onLockFailed(Exception e);

    StateMachine onError(StateMachine stateMachine, Exception e);
}
