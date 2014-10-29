package pure.fsm.core.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;
import pure.fsm.core.state.ErrorFinalState;

public abstract class BaseStateMachineCallback implements StateMachineCallback {

    private final static Logger LOG = LoggerFactory.getLogger(BaseStateMachineCallback.class);

    @Override
    @SuppressWarnings("unchecked")
    public Context onError(Context context, StateMachine stateMachine, Exception e) {
        LOG.error("On Error, returning state machine in error state.", e);

        Context transitioned = context.transition(new ErrorFinalState(), null);

        transitioned.setException(e);

        return transitioned;
    }

    @Override
    public void onLockFailed(Exception e) {
        LOG.error("On onLockFailed, not doing anything", e);
    }
}
