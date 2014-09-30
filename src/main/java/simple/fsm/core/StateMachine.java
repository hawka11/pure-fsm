package simple.fsm.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.event.Event;
import simple.fsm.core.state.ErrorFinalState;
import simple.fsm.core.state.State;

public class StateMachine<T extends Context> {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);
    private final String stateMachineId;
    private final State currentState;
    private final T context;

    public StateMachine(String stateMachineId, State currentState, T context) {
        this.stateMachineId = stateMachineId;
        this.currentState = currentState;
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public StateMachine<T> handleEvent(Event event) {
        try {
            State newState = currentState.handle(context, event);

            currentState.onExit(context, event);

            newState.onEntry(context, event, currentState);

            return new StateMachine(stateMachineId, newState, context);
        } catch (Exception e) {
            LOG.error("Error handling event [{}]", event);
            return new StateMachine(stateMachineId, new ErrorFinalState(e), context);
        }
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
}
