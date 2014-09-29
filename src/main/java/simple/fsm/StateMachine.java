package simple.fsm;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.event.Event;
import simple.fsm.state.ErrorFinalState;
import simple.fsm.state.State;

public class StateMachine {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);
    private final State currentState;
    private final Context context;

    public StateMachine(State currentState, Context context) {
        this.currentState = currentState;
        this.context = context;
    }

    public State handleEvent(Event event) {
        try {
            return currentState.handle(context, event);
        } catch (Exception e) {
            LOG.error("Error handling event [{}]", event);
            return new ErrorFinalState();
        }
    }
}
