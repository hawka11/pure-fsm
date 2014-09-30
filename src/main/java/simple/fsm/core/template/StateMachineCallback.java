package simple.fsm.core.template;

import simple.fsm.core.StateMachine;

public interface StateMachineCallback {

    StateMachine doWith(StateMachine stateMachine);

    void lockFailed(Exception e);

    StateMachine onError(StateMachine stateMachine, Exception e);
}
