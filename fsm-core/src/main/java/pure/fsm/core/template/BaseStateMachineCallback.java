package pure.fsm.core.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.StateMachine;
import pure.fsm.core.state.ErrorFinalState;

import static com.google.common.collect.Lists.newArrayList;
import static pure.fsm.core.context.ExceptionContext.withException;

public abstract class BaseStateMachineCallback implements StateMachineCallback {

    private final static Logger LOG = LoggerFactory.getLogger(BaseStateMachineCallback.class);

    @Override
    @SuppressWarnings("unchecked")
    public Transition onError(Transition prevTransition, StateMachine stateMachine, Exception e) {
        LOG.error("On Error, returning state machine in error state.", e);

        return prevTransition
                .transitionTo(new ErrorFinalState(), null, newArrayList(withException(e)));
    }

    @Override
    public void onLockFailed(Exception e) {
        LOG.error("On onLockFailed, not doing anything", e);
    }
}
