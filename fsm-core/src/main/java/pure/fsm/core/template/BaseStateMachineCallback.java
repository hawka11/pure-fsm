package pure.fsm.core.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;
import pure.fsm.core.state.ErrorFinalState;

import static pure.fsm.core.trait.ExceptionTrait.withException;

public abstract class BaseStateMachineCallback implements StateMachineCallback {

    private final static Logger LOG = LoggerFactory.getLogger(BaseStateMachineCallback.class);

    @Override
    @SuppressWarnings("unchecked")
    public Context onError(Context context, StateMachine stateMachine, Exception e) {
        LOG.error("On Error, returning state machine in error state.", e);

        return context
                .addTrait(withException(e))
                .transition(new ErrorFinalState(), null)
                .context;
    }

    @Override
    public void onLockFailed(Exception e) {
        LOG.error("On onLockFailed, not doing anything", e);
    }
}
