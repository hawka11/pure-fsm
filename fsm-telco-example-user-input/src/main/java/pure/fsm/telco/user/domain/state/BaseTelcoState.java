package pure.fsm.telco.user.domain.state;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.state.BaseNonFinalState;
import pure.fsm.core.state.TimedOutFinalState;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.user.domain.event.ConfirmPinEvent;
import pure.fsm.telco.user.domain.event.RequestAcceptedEvent;
import pure.fsm.telco.user.domain.event.RequestPinEvent;
import pure.fsm.telco.user.domain.event.TelcoEventVisitor;

import static pure.fsm.core.Transition.transition;
import static pure.fsm.core.trait.MessageTrait.withMessage;

public class BaseTelcoState extends BaseNonFinalState implements TelcoEventVisitor {

    private final DistributedResourceFactory resourceFactory;

    protected BaseTelcoState(DistributedResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public DistributedResourceFactory resourceFactory() {
        return resourceFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Transition handle(Context context, Event event) {
        return event.accept(context, this);
    }

    @Override
    public Transition visit(Context context, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        Transition transition = transition(this, context);
        if (isTimeout(context)) {
            transition = context
                    .addTrait(withMessage("because timedout"))
                    .transition(new TimedOutFinalState(), timeoutTickEvent);
        }

        return transition;
    }

    @Override
    public Transition accept(Context context, RequestPinEvent requestPinEvent) {
        return nonHandledEvent(context, requestPinEvent);
    }

    @Override
    public Transition accept(Context context, ConfirmPinEvent confirmPinEvent) {
        return nonHandledEvent(context, confirmPinEvent);
    }

    @Override
    public Transition accept(Context context, RequestAcceptedEvent requestAcceptedEvent) {
        return nonHandledEvent(context, requestAcceptedEvent);
    }
}
