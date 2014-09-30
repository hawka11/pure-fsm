package simple.fsm.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.event.Event;
import simple.fsm.core.state.ErrorFinalState;
import simple.fsm.core.state.State;

public class StateMachine {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);
    private State currentState;
    private final Context context;

    public StateMachine(State currentState, Context context) {
        this.currentState = currentState;
        this.context = context;
    }

    public void handleEvent(Event event) {
        try {
            State newState = currentState.handle(context, event);

            currentState.onExit(context, event);

            newState.onEntry(context, event, currentState);

            currentState = newState;
        } catch (Exception e) {
            LOG.error("Error handling event [{}]", event);
            currentState = new ErrorFinalState(e);
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    public Context getContext() {
        return context;
    }
}
