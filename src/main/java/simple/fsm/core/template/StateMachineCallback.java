package simple.fsm.core.template;

import simple.fsm.core.StateMachine;

public interface StateMachineCallback {
    void doWith(StateMachine stateMachine);

    void onError(Exception e);
}
