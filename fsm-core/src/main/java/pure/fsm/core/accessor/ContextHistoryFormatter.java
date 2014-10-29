package pure.fsm.core.accessor;

import pure.fsm.core.Context;

import static java.lang.String.format;

public class ContextHistoryFormatter {

    public static final ContextHistoryFormatter HISTORY_FORMATTER = new ContextHistoryFormatter();

    public String toTransitionString(Context context) {

        return format("\n+++++ State Machine Transition history stateMachineId [%s] =>",
                context.getStateMachineId()) + "\n" + toContextString(context, calcNumTransitions(context, 1)) + "\n\n";
    }

    private int calcNumTransitions(Context context, int count) {

        return context.previous().isPresent() ? calcNumTransitions(context.previous().get(), count + 1) : count;
    }

    private String toContextString(Context context, int indent) {
        StringBuilder sb = new StringBuilder();

        context.previous().ifPresent(prev -> sb.append(toContextString(prev, indent - 1)));

        sb.append(format("%" + indent + "s", " ")).append(format("State[%s], Transitioned[%s], event[%s], msg[%s]",
                context.getCurrentState().getClass().getSimpleName(),
                context.getTransitioned(),
                context.getEvent(),
                context.getMessage()));

        return sb.append("\n").toString();
    }
}
