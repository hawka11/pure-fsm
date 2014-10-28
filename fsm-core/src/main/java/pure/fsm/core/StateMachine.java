package pure.fsm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.event.Event;
import pure.fsm.core.state.ErrorFinalState;
import pure.fsm.core.state.State;

public class StateMachine {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);

    public final static StateMachine STATE_MACHINE_INSTANCE = new StateMachine();

    private StateMachine() {
    }

    @SuppressWarnings("unchecked")
    public Context handleEvent(Context context, Event event) {
        final State currentState = context.getCurrentState();
        final String stateMachineId = context.getStateMachineId();

        State newState;
        Context transitionedContext;

        try {
            newState = currentState.handle(context, event);

            currentState.onExit(context, event);

            transitionedContext = context.transition(newState);

            newState.onEntry(transitionedContext, event, currentState);
        } catch (Exception e) {
            LOG.error("SM [" + stateMachineId + "], Error handling event [" + event + "]", e);

            newState = new ErrorFinalState();

            transitionedContext = context.transition(newState);
            transitionedContext.setException(e);

            newState.onEntry(transitionedContext, event, currentState);
        }

        return transitionedContext;
    }
}
