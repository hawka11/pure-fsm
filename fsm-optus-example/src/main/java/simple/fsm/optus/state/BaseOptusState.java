package simple.fsm.optus.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;
import simple.fsm.core.event.TimeoutTickEvent;
import simple.fsm.core.state.State;
import simple.fsm.core.state.StateFactory;
import simple.fsm.core.state.TimedOutFinalState;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optus.event.CancelRechargeEvent;
import simple.fsm.optus.event.OptusEventVisitor;
import simple.fsm.optus.event.RechargeAcceptedEvent;
import simple.fsm.optus.event.RequestRechargeEvent;

import java.time.LocalDateTime;

public class BaseOptusState implements State, OptusEventVisitor {

    @Override
    public OptusStateFactory factory() {
        return new OptusStateFactory();
    }

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

    @Override
    public State visit(Context context, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        context.setMessage("because timedout");

        return isTimeout(context) ? new TimedOutFinalState() : this;
    }

    protected LocalDateTime getTimeoutDateTime(Context context) {
        //example timeout is 5 seconds
        return context.getTransitioned().plusSeconds(5);
    }

    protected boolean isTimeout(Context context) {

        return LocalDateTime.now().isAfter(getTimeoutDateTime(context));
    }
}
