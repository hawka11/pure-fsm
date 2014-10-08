package simple.fsm.core.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.StateMachine;
import simple.fsm.core.state.ErrorFinalState;

public abstract class BaseStateMachineCallback implements StateMachineCallback {

    private final static Logger LOG = LoggerFactory.getLogger(BaseStateMachineCallback.class);

    @Override
    @SuppressWarnings("unchecked")
    public StateMachine onError(StateMachine stateMachine, Exception e) {
        LOG.error("On Error, returning state machine in error state.", e);
        stateMachine.getContext().setException(e);
        return new StateMachine(stateMachine.getStateMachineId(), new ErrorFinalState(), stateMachine.getContext());
    }

    @Override
    public void onLockFailed(Exception e) {
        LOG.error("On onLockFailed, not doing anything", e);
    }
}
