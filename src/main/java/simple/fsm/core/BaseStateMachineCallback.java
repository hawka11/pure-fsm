package simple.fsm.core;

public abstract class BaseStateMachineCallback implements StateMachineCallback {

    @Override
    public void onError(Exception e) {
        //log and handle default
    }
}
