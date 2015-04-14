package pure.fsm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.context.MostRecentTrait;
import pure.fsm.core.event.Event;
import pure.fsm.core.state.ErrorFinalState;
import pure.fsm.core.state.State;

import static pure.fsm.core.trait.ExceptionTrait.withException;

public class StateMachine {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);

    public final static StateMachine STATE_MACHINE_INSTANCE = new StateMachine();

    private StateMachine() {
    }

    @SuppressWarnings("unchecked")
    public Context handleEvent(Context context, Event event) {
        final State currentState = MostRecentTrait.currentState(context);
        final String stateMachineId = context.stateMachineId;

        Transition transition;

        try {
            transition = currentState.handle(context, event);

            currentState.onExit(context, event);

            transition.state.onEntry(transition.context, event, currentState);
        } catch (Exception e) {
            LOG.error("SM [" + stateMachineId + "], Error handling event [" + event + "]", e);

            transition = context
                    .addTrait(withException(e))
                    .transition(new ErrorFinalState(), event);

            transition.state.onEntry(transition.context, event, currentState);
        }

        return transition.context;
    }
}
