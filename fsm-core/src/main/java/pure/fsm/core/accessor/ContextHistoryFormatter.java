package pure.fsm.core.accessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.context.ContextMessage;

import java.util.List;

import static java.lang.String.format;
import static pure.fsm.core.context.InitialContext.initialContext;

public class ContextHistoryFormatter implements OnCleanupListener {

    private final static Logger LOG = LoggerFactory.getLogger(ContextHistoryFormatter.class);

    public static final ContextHistoryFormatter HISTORY_FORMATTER = new ContextHistoryFormatter();

    public String toTransitionString(Transition transition) {

        return format("\n\n+++++ State Machine Transition history stateMachineId [%s] +++++ =>",
                initialContext(transition.getContext()).stateMachineId) + "\n" + toContextString(transition, calcNumTransitions(transition, 1));
    }

    private int calcNumTransitions(Transition transition, int count) {

        return transition.previous().isPresent() ? calcNumTransitions(transition.previous().get(), count + 1) : count;
    }

    private String toContextString(Transition lastTransition, int indent) {
        StringBuilder sb = new StringBuilder();

        lastTransition.previous().ifPresent(prev -> sb.append(toContextString(prev, indent - 1)));

        final List<ContextMessage> msg = lastTransition.getContext().getContextsOfType(ContextMessage.class);

        sb.append(format("%" + indent + "s", " ")).append(format("State[%s], Transitioned[%s], event[%s], msg[%s]",
                lastTransition.getState().getClass().getName(),
                lastTransition.getTransitioned(),
                lastTransition.getEvent(),
                "TODO msg"));

        return sb.append("\n").toString();
    }

    @Override
    public void onCleanup(Transition transition) {
        LOG.info(toTransitionString(transition));
    }
}
