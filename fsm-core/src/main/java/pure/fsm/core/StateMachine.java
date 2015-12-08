package pure.fsm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static pure.fsm.core.FinalState.ERROR_FINAL_STATE;
import static pure.fsm.core.context.ExceptionContext.withException;

public abstract class StateMachine {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);

    private Map<Class<?>, HandleEvent> defByState = newHashMap();

    @SuppressWarnings("unchecked")
    public Transition handleEvent(Transition last, Object event) {
        final Object current = last.getState();
        final Context context = last.getContext();
        final String stateMachineId = context.stateMachineId();

        Transition next;

        try {

            final HandleEvent handleEvent = defByState.get(current.getClass());

            next = handleEvent.handle(last, event);

        } catch (Exception e) {
            LOG.error("SM [" + stateMachineId + "], Error handling event [" + event + "]", e);

            final Context updatedContext = context.appendState(withException(e));

            next = Transition.To(ERROR_FINAL_STATE, event, updatedContext);
        }

        return next;
    }

    protected void when(Object state, HandleEvent handleEvent) {
        defByState.put(state.getClass(), handleEvent);
    }

    protected void when(Class<?> state, HandleEvent handleEvent) {
        defByState.put(state, handleEvent);
    }

    protected Transition go(Object state, Object event, Context context) {
        return Transition.To(state, event, context);
    }

    protected Transition stay(Object state, Object event, Context context) {
        return go(state, event, context);
    }

    protected Transition error(Object event, Context context) {
        return Transition.To(ERROR_FINAL_STATE, event, context);
    }

    @FunctionalInterface
    public interface HandleEvent {
        Transition handle(Transition last, Object event);
    }
}
