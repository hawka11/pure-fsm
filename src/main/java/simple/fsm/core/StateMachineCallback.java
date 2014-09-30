package simple.fsm.core;

public interface StateMachineCallback {
    void doWith(StateMachine stateMachine);

    void onError(Exception e);
}
