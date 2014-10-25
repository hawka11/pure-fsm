package pure.fsm.telco.user.domain.state;

import pure.fsm.core.Context;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.state.BaseNonFinalState;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;
import pure.fsm.core.state.TimedOutFinalState;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.user.domain.TelcoRechargeContext;
import pure.fsm.telco.user.domain.event.ConfirmPinEvent;
import pure.fsm.telco.user.domain.event.RequestAcceptedEvent;
import pure.fsm.telco.user.domain.event.RequestPinEvent;
import pure.fsm.telco.user.domain.event.TelcoEventVisitor;

public class BaseTelcoState extends BaseNonFinalState implements TelcoEventVisitor {

    private final DistributedResourceFactory resourceFactory;
    private final StateFactory telcoStateFactory;

    protected BaseTelcoState(StateFactory telcoStateFactory, DistributedResourceFactory resourceFactory) {
        this.telcoStateFactory = telcoStateFactory;
        this.resourceFactory = resourceFactory;
    }

    @Override
    public StateFactory factory() {
        return telcoStateFactory;
    }

    public DistributedResourceFactory resourceFactory() {
        return resourceFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public State handle(Context context, Event event) {
        return event.accept(context, this);
    }

    @Override
    public State visit(Context context, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        if (isTimeout(context)) {
            context.setMessage("because timedout");
            return new TimedOutFinalState();
        }

        return this;
    }

    @Override
    public State accept(TelcoRechargeContext context, RequestPinEvent requestPinEvent) {
        return nonHandledEvent(context, requestPinEvent);
    }

    @Override
    public State accept(TelcoRechargeContext context, ConfirmPinEvent confirmPinEvent) {
        return nonHandledEvent(context, confirmPinEvent);
    }

    @Override
    public State accept(TelcoRechargeContext context, RequestAcceptedEvent requestAcceptedEvent) {
        return nonHandledEvent(context, requestAcceptedEvent);
    }
}
