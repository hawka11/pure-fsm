package pure.fsm.core.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;
import pure.fsm.core.Transition;
import pure.fsm.core.state.ErrorFinalState;

import java.util.Optional;

import static pure.fsm.core.context.ExceptionContext.withException;

public class DefaultStateMachineCallback implements StateMachineCallback {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultStateMachineCallback.class);

    private final HandleContextWithTransition handler;

    private DefaultStateMachineCallback(HandleContextWithTransition handler) {
        this.handler = handler;
    }

    public static DefaultStateMachineCallback handleWithTransition(HandleContextWithTransition handler) {

        return new DefaultStateMachineCallback(handler);
    }

    public static DefaultStateMachineCallback handleWithinLock(HandleContextWithoutTransition handler) {
        return new DefaultStateMachineCallback((context, stateMachine) -> {
            handler.doWith(context, stateMachine);
            return null;
        });
    }

    @Override
    public Optional<Transition> doWith(Transition prevTransition, StateMachine stateMachine) {
        final Transition nextTransition = handler.doWith(prevTransition, stateMachine);
        return Optional.ofNullable(nextTransition);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Transition onError(Transition prevTransition, StateMachine stateMachine, Exception e) {
        LOG.error("On Error, returning state machine in error state.", e);

        final Context updatedContext = prevTransition.getContext().appendState(withException(e));

        return Transition.To(new ErrorFinalState(), null, updatedContext);
    }

    @Override
    public void onLockFailed(Exception e) {
        LOG.error("On onLockFailed, not doing anything", e);
    }

    @FunctionalInterface
    public interface HandleContextWithTransition {
        Transition doWith(Transition prevTransition, StateMachine stateMachine);
    }

    @FunctionalInterface
    public interface HandleContextWithoutTransition {
        void doWith(Transition prevTransition, StateMachine stateMachine);
    }
}
