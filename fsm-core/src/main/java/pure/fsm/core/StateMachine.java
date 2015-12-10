package pure.fsm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.collect.Maps.newHashMap;
import static pure.fsm.core.FinalState.ERROR_FINAL_STATE;
import static pure.fsm.core.context.ExceptionContext.withException;

public abstract class StateMachine<T> {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);

    private Map<Class<?>, HandleEvent<T>> defByState = newHashMap();
    private Map<Class<?>, Map<Class<?>, Consumer<Transition>>> defByTransition = newHashMap();

    @SuppressWarnings("unchecked")
    public Transition handleEvent(Transition last, T event) {
        final Object current = last.getState();
        final Context context = last.getContext();
        final String stateMachineId = context.stateMachineId();

        Transition next;

        try {

            final HandleEvent<T> handleEvent = defByState.get(current.getClass());

            if (handleEvent != null) {
                next = handleEvent.handle(last, event);
            } else {
                next = unhandled(last, event);
            }

        } catch (Exception e) {
            LOG.error("SM [" + stateMachineId + "], Error handling event [" + event + "]", e);

            next = onError(event, context, e);
        }

        //TODO: rename this method
        next = last.setNextTransition(next);

        invokeOnTransitionListeners(last, next);

        return next;
    }

    private void invokeOnTransitionListeners(Transition last, Transition next) {
        defByTransition.entrySet().stream().forEach(it -> {
            if (it.getKey().isAssignableFrom(last.getState().getClass())) {
                it.getValue().entrySet().stream().forEach(ij -> {
                    if (ij.getKey().isAssignableFrom(next.getState().getClass())) {
                        ij.getValue().accept(next);
                    }
                });
            }
        });
    }

    protected Transition onError(T event, Context context, Exception e) {
        final Context updatedContext = context.appendState(withException(e));
        return Transition.To(ERROR_FINAL_STATE, event, updatedContext);
    }

    protected Transition unhandled(Transition last, T event) {
        LOG.error("unhandled event {}", event);
        return Transition.To(ERROR_FINAL_STATE, event, last.getContext());
    }

    protected void when(Object state, HandleEvent<T> handleEvent) {
        defByState.put(state.getClass(), handleEvent);
    }

    protected void when(Class<?> state, HandleEvent<T> handleEvent) {
        defByState.put(state, handleEvent);
    }

    protected void onTransition(Object state, Object next, Consumer<Transition> f) {
        onTransition(state.getClass(), next.getClass(), f);
    }

    protected void onTransition(Class<?> state, Class<?> next, Consumer<Transition> f) {
        if (!defByTransition.containsKey(state)) defByTransition.put(state, newHashMap());
        defByTransition.get(state).put(next, f);
    }

    protected Transition go(Object state, T event, Context context) {
        return Transition.To(state, event, context);
    }

    protected Transition stay(Object state, T event, Context context) {
        return go(state, event, context);
    }

    protected Transition error(T event, Context context) {
        return Transition.To(ERROR_FINAL_STATE, event, context);
    }

    @FunctionalInterface
    public interface HandleEvent<T> {
        Transition handle(Transition last, T event);
    }
}
