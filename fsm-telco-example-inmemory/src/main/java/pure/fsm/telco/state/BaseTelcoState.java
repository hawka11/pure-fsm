package pure.fsm.telco.state;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.state.BaseNonFinalState;
import pure.fsm.core.state.TimedOutFinalState;
import pure.fsm.telco.event.CancelRechargeEvent;
import pure.fsm.telco.event.RechargeAcceptedEvent;
import pure.fsm.telco.event.RequestRechargeEvent;
import pure.fsm.telco.event.TelcoEventVisitor;

import static pure.fsm.core.Transition.transition;
import static pure.fsm.core.trait.MessageTrait.withMessage;

public class BaseTelcoState extends BaseNonFinalState implements TelcoEventVisitor {

    @Override
    @SuppressWarnings("unchecked")
    public Transition handle(Context context, Event event) {
        return event.accept(context, this);
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
    public Transition visit(Context context, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        context.addTrait(withMessage("because timedout"));

        return isTimeout(context) ? context.transition(new TimedOutFinalState(), timeoutTickEvent) : transition(this, context);
    }
}
