package pure.fsm.core.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;
import pure.fsm.core.Transition;
import pure.fsm.core.state.ErrorFinalState;

import static pure.fsm.core.context.ExceptionContext.withException;

public class DefaultStateMachineCallable<T> implements StateMachineCallable<T> {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultStateMachineCallable.class);

    private final HandleWithinLockCallable<T> handler;

    private DefaultStateMachineCallable(HandleWithinLockCallable<T> handler) {
        this.handler = handler;
    }

    public static DefaultStateMachineCallable<Transition> handleWithTransition(HandleWithinLockCallable<Transition> handler) {
        return new DefaultStateMachineCallable<>(handler);
    }

    public static <T> DefaultStateMachineCallable<T> handleWithinLock(HandleWithinLockCallable<T> handler) {
        return new DefaultStateMachineCallable<>(handler);
    }

    @Override
    public T doWith(Transition prevTransition, StateMachine stateMachine) {
        return handler.doWith(prevTransition, stateMachine);
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
    public interface HandleWithinLockCallable<T> {
        T doWith(Transition prevTransition, StateMachine stateMachine);
    }
}
