package pure.fsm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.event.Event;
import pure.fsm.core.state.ErrorFinalState;
import pure.fsm.core.state.State;

import static com.google.common.collect.Lists.newArrayList;
import static pure.fsm.core.trait.ExceptionContext.withException;
import static pure.fsm.core.trait.InitialContext.initialContext;

public class StateMachine {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);

    public final static StateMachine STATE_MACHINE_INSTANCE = new StateMachine();

    private StateMachine() {
    }

    @SuppressWarnings("unchecked")
    public Transition handleEvent(Transition prevTransition, Event event) {
        final State currentState = prevTransition.getState();
        final String stateMachineId = initialContext(prevTransition).stateMachineId;

        Transition transition;

        try {
            transition = currentState.handle(prevTransition, event);

            currentState.onExit(prevTransition, event);

            transition.getState().onEntry(transition, event, currentState);
        } catch (Exception e) {
            LOG.error("SM [" + stateMachineId + "], Error handling event [" + event + "]", e);

            transition = prevTransition
                    .transitionTo(new ErrorFinalState(), event, newArrayList(withException(e)));

            transition.getState().onEntry(transition, event, currentState);
        }

        return transition;
    }
}
