package pure.fsm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.state.ErrorFinalState;
import pure.fsm.core.state.State;

import static pure.fsm.core.context.ExceptionContext.withException;
import static pure.fsm.core.context.InitialContext.initialContext;

public class StateMachine {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachine.class);

    public final static StateMachine STATE_MACHINE_INSTANCE = new StateMachine();

    private StateMachine() {
    }

    @SuppressWarnings("unchecked")
    public Transition handleEvent(Transition prevTransition, Event event) {
        final State currentState = prevTransition.getState();
        final Context context = prevTransition.getContext();
        final String stateMachineId = initialContext(context).stateMachineId;

        Transition newTransition;

        try {
            if (TimeoutTickEvent.class.equals(event.getClass())) {
                final TimeoutTickEvent timeoutTicker = TimeoutTickEvent.class.cast(event);
                newTransition = currentState.handle(prevTransition, timeoutTicker);
            } else {
                newTransition = currentState.handle(context, event);
            }

            currentState.onExit(context, event);

            newTransition.getState().onEntry(context, event, currentState);
        } catch (Exception e) {
            LOG.error("SM [" + stateMachineId + "], Error handling event [" + event + "]", e);

            final Context updatedContext = prevTransition.getContext().appendState(withException(e));

            newTransition = Transition.To(new ErrorFinalState(), event, updatedContext);

            newTransition.getState().onEntry(updatedContext, event, currentState);
        }

        return newTransition;
    }
}
