package simple.fsm.core.template;

import simple.fsm.core.StateMachine;
import simple.fsm.core.state.ErrorFinalState;

public abstract class BaseStateMachineCallback implements StateMachineCallback {

    @Override
    @SuppressWarnings("unchecked")
    public StateMachine onError(StateMachine stateMachine, Exception e) {
        //log and handle default
        return new StateMachine(stateMachine.getStateMachineId(), new ErrorFinalState(e), stateMachine.getContext());
    }

    @Override
    public void lockFailed(Exception e) {

    }
}
