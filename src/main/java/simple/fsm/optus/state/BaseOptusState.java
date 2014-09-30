package simple.fsm.optus.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;
import simple.fsm.core.state.State;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optus.event.CancelRechargeEvent;
import simple.fsm.optus.event.OptusEventVisitor;
import simple.fsm.optus.event.RechargeAcceptedEvent;
import simple.fsm.optus.event.RequestRechargeEvent;

public class BaseOptusState implements State, OptusEventVisitor {

    @Override
    @SuppressWarnings("unchecked")
    public State handle(Context context, Event event) {
        return event.accept(context, this);
    }

    @Override
    public void onExit(Context context, Event event) {

    }

    @Override
    public void onEntry(Context context, Event event, State prevState) {

    }

    @Override
    public State visit(OptusRechargeContext context, RequestRechargeEvent requestRechargeEvent) {
        throw new IllegalStateException("not handled by state");
    }

    @Override
    public State visit(OptusRechargeContext context, CancelRechargeEvent cancelRechargeEvent) {
        throw new IllegalStateException("not handled by state");
    }

    @Override
    public State visit(OptusRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        throw new IllegalStateException("not handled by state");
    }
}
