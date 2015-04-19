package pure.fsm.telco.state;

import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.state.BaseNonFinalState;
import pure.fsm.core.state.TimedOutFinalState;
import pure.fsm.telco.event.CancelRechargeEvent;
import pure.fsm.telco.event.RechargeAcceptedEvent;
import pure.fsm.telco.event.RequestRechargeEvent;
import pure.fsm.telco.event.TelcoEventVisitor;

import static com.google.common.collect.Lists.newArrayList;
import static pure.fsm.core.context.MessageContext.withMessage;

public class BaseTelcoState extends BaseNonFinalState implements TelcoEventVisitor {

    @Override
    @SuppressWarnings("unchecked")
    public Transition handle(Transition prevTransition, Event event) {
        return event.accept(prevTransition, this);
    }

    @Override
    public Transition visit(Transition transition, RequestRechargeEvent requestRechargeEvent) {
        return nonHandledEvent(transition, requestRechargeEvent);
    }

    @Override
    public Transition visit(Transition transition, CancelRechargeEvent cancelRechargeEvent) {
        return nonHandledEvent(transition, cancelRechargeEvent);
    }

    @Override
    public Transition visit(Transition transition, RechargeAcceptedEvent rechargeAcceptedEvent) {
        return nonHandledEvent(transition, rechargeAcceptedEvent);
    }

    @Override
    public Transition visit(Transition prevTransition, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        return isTimeout(prevTransition)
                ? prevTransition.transitionTo(new TimedOutFinalState(), timeoutTickEvent, newArrayList(withMessage("because timedout")))
                : prevTransition;
    }
}
