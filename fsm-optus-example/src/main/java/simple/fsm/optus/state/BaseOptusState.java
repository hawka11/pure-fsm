package simple.fsm.optus.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;
import simple.fsm.core.event.TimeoutTickEvent;
import simple.fsm.core.state.BaseNonFinalState;
import simple.fsm.core.state.State;
import simple.fsm.core.state.StateFactory;
import simple.fsm.core.state.TimedOutFinalState;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optus.event.CancelRechargeEvent;
import simple.fsm.optus.event.OptusEventVisitor;
import simple.fsm.optus.event.RechargeAcceptedEvent;
import simple.fsm.optus.event.RequestRechargeEvent;

public class BaseOptusState extends BaseNonFinalState implements OptusEventVisitor {

    @Override
    public StateFactory factory() {
        return new OptusStateFactory();
    }

    @Override
    @SuppressWarnings("unchecked")
    public State handle(Context context, Event event) {
        return event.accept(context, this);
    }

    @Override
    public State visit(OptusRechargeContext context, RequestRechargeEvent requestRechargeEvent) {
        return nonHandledEvent(context, requestRechargeEvent);
    }

    @Override
    public State visit(OptusRechargeContext context, CancelRechargeEvent cancelRechargeEvent) {
        return nonHandledEvent(context, cancelRechargeEvent);
    }

    @Override
    public State visit(OptusRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        return nonHandledEvent(context, rechargeAcceptedEvent);
    }

    @Override
    public State visit(Context context, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        context.setMessage("because timedout");

        return isTimeout(context) ? new TimedOutFinalState() : this;
    }
}
