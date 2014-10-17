package simple.fsm.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.event.Event;
import simple.fsm.core.state.ErrorFinalState;
import simple.fsm.core.state.State;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class StateMachine<T extends Context> {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);
    private final String stateMachineId;
    private final State currentState;
    private final T context;
    private final StateMachine<T> previous;

    public StateMachine(String stateMachineId, State currentState, T context) {
        this(stateMachineId, currentState, context, null);
    }

    public StateMachine(String stateMachineId, State currentState, T context, StateMachine<T> previous) {
        this.stateMachineId = stateMachineId;
        this.currentState = currentState;
        this.context = context;
        this.previous = previous;
    }

    @SuppressWarnings("unchecked")
    public StateMachine<T> handleEvent(Event event) {
        State newState;
        Context transitionedContext;
        try {
            newState = currentState.handle(context, event);

            currentState.onExit(context, event);

            transitionedContext = context.transition();

            newState.onEntry(transitionedContext, event, currentState);
        } catch (Exception e) {
            LOG.error("SM [" + stateMachineId + "], Error handling event [" + event + "]", e);

            transitionedContext = context.transition();
            transitionedContext.setException(e);

            newState = new ErrorFinalState();
            newState.onEntry(transitionedContext, event, currentState);
        }

        return new StateMachine(stateMachineId, newState, transitionedContext, this);
    }

    public String getStateMachineId() {
        return stateMachineId;
    }

    public State getCurrentState() {
        return currentState;
    }

    public T getContext() {
        return context;
    }

    public Optional<StateMachine<T>> previous() {
        return ofNullable(previous);
    }
}
