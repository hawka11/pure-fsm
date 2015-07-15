package pure.fsm.end2end.state;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.state.BaseNonFinalState;
import pure.fsm.core.state.TimedOutFinalState;
import pure.fsm.end2end.event.CancelRechargeEvent;
import pure.fsm.end2end.event.RechargeAcceptedEvent;
import pure.fsm.end2end.event.RequestRechargeEvent;
import pure.fsm.end2end.event.TelcoEventVisitor;

import static pure.fsm.core.context.ContextMessage.withMessage;

public class BaseTelcoState extends BaseNonFinalState implements TelcoEventVisitor {

    @Override
    @SuppressWarnings("unchecked")
    public Transition handle(Context context, Event event) {
        return event.accept(context, this);
    }

    @Override
    public Transition handle(Transition prevTransition, TimeoutTickEvent event) {
        return event.accept(prevTransition, this);
    }

    @Override
    public Transition visit(Context context, RequestRechargeEvent requestRechargeEvent) {
        return nonHandledEvent(context, requestRechargeEvent);
    }

    @Override
    public Transition visit(Context context, CancelRechargeEvent cancelRechargeEvent) {
        return nonHandledEvent(context, cancelRechargeEvent);
    }

    @Override
    public Transition visit(Context context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        return nonHandledEvent(context, rechargeAcceptedEvent);
    }

    @Override
    public Transition visit(Transition transition, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        return isTimeout(transition)
                ? Transition.To(new TimedOutFinalState(), timeoutTickEvent, transition.getContext().appendState(withMessage("because timedout")))
                : transition;
    }
}
