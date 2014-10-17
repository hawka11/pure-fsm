package simple.fsm.telco.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;
import simple.fsm.core.event.TimeoutTickEvent;
import simple.fsm.core.state.BaseNonFinalState;
import simple.fsm.core.state.State;
import simple.fsm.core.state.StateFactory;
import simple.fsm.core.state.TimedOutFinalState;
import simple.fsm.telco.TelcoRechargeContext;
import simple.fsm.telco.event.CancelRechargeEvent;
import simple.fsm.telco.event.TelcoEventVisitor;
import simple.fsm.telco.event.RechargeAcceptedEvent;
import simple.fsm.telco.event.RequestRechargeEvent;

public class BaseTelcoState extends BaseNonFinalState implements TelcoEventVisitor {

    @Override
    public StateFactory factory() {
        return new TelcoStateFactory();
    }

    @Override
    @SuppressWarnings("unchecked")
    public State handle(Context context, Event event) {
        return event.accept(context, this);
    }

    @Override
    public State visit(TelcoRechargeContext context, RequestRechargeEvent requestRechargeEvent) {
        return nonHandledEvent(context, requestRechargeEvent);
    }

    @Override
    public State visit(TelcoRechargeContext context, CancelRechargeEvent cancelRechargeEvent) {
        return nonHandledEvent(context, cancelRechargeEvent);
    }

    @Override
    public State visit(TelcoRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        return nonHandledEvent(context, rechargeAcceptedEvent);
    }

    @Override
    public State visit(Context context, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        context.setMessage("because timedout");

        return isTimeout(context) ? new TimedOutFinalState() : this;
    }
}
