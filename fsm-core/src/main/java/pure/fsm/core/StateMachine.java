package pure.fsm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static pure.fsm.core.FinalState.ERROR_FINAL_STATE;
import static pure.fsm.core.FinalState.TIMEOUT_ERROR_FINAL_STATE;
import static pure.fsm.core.context.ExceptionContext.withException;

public abstract class StateMachine<E> {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);

    private final Map<Class<?>, HandleEvent<E>> defByState = newHashMap();
    private final Map<Class<?>, Map<Class<?>, OnTransition<E>>> defByTransition = newHashMap();

    @SuppressWarnings("unchecked")
    public Transition handleEvent(Transition last, E event) {
        final Object current = last.getState();
        final Context context = last.getContext();
        final String stateMachineId = context.stateMachineId();

        Transition next;

        try {

            final HandleEvent<E> handleEvent = defByState.get(current.getClass());

            if (handleEvent != null) {
                next = handleEvent.handle(last, event);
            } else {
                LOG.error("unhandled event {}", event);
                next = unhandled(last, event);
            }

        } catch (Exception e) {

            LOG.error("SM [" + stateMachineId + "], Error handling event [" + event + "]", e);

            next = onError(event, context, e);
        }

        next = next.setPrevious(last);

        invokeOnTransitionListeners(last, next, event);

        return next;
    }

    private void invokeOnTransitionListeners(Transition last, Transition next, E event) {
        defByTransition.entrySet().stream().forEach(it -> {
            if (it.getKey().isAssignableFrom(last.getState().getClass())) {
                it.getValue().entrySet().stream().forEach(ij -> {
                    if (ij.getKey().isAssignableFrom(next.getState().getClass())) {
                        ij.getValue().onTransition(next, event);
                    }
                });
            }
        });
    }

    protected Transition onError(E event, Context context, Exception e) {
        return error(event, context.appendState(withException(e)));
    }

    protected Transition unhandled(Transition last, E event) {
        if (FinalState.class.isAssignableFrom(last.getState().getClass())) {
            LOG.info("ignoring event {}", event);
            return stay(last.getState(), event, last.getContext());
        } else {
            return unhandledError(last, event);
        }
    }

    protected Transition unhandledError(Transition last, E event) {
        return error(event, last.getContext());
    }

    protected void when(Object state, HandleEvent<E> handleEvent) {
        when(state.getClass(), handleEvent);
    }

    protected void when(Class<?> state, HandleEvent<E> handleEvent) {
        defByState.put(state, handleEvent);
    }

    protected void onTransition(Object state, Object next, OnTransition<E> f) {
        onTransition(state.getClass(), next.getClass(), f);
    }

    protected void onTransition(Class<?> state, Class<?> next, OnTransition<E> f) {
        if (!defByTransition.containsKey(state)) defByTransition.put(state, newHashMap());
        defByTransition.get(state).put(next, f);
    }

    protected Transition go(Object state, E event, Context context) {
        return Transition.To(state, event, context);
    }

    protected Transition stay(Object state, E event, Context context) {
        return go(state, event, context);
    }

    protected Transition error(E event, Context context) {
        return go(ERROR_FINAL_STATE, event, context);
    }

    protected Transition timeout(E event, Context context) {
        return go(TIMEOUT_ERROR_FINAL_STATE, event, context);
    }

    @FunctionalInterface
    public interface HandleEvent<E> {
        Transition handle(Transition last, E event);
    }

    @FunctionalInterface
    public interface OnTransition<E> {
        void onTransition(Transition next, E event);
    }
}
