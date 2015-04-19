package pure.fsm.telco.user.domain.state;

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

import static com.google.common.collect.Lists.newArrayList;
import static pure.fsm.core.trait.MessageContext.withMessage;

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
    public Transition handle(Transition prevTransition, Event event) {
        return event.accept(prevTransition, this);
    }

    @Override
    public Transition visit(Transition prevTransition, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        Transition transition = prevTransition.transitionTo(this, timeoutTickEvent);
        if (isTimeout(prevTransition)) {
            transition = prevTransition.transitionTo(new TimedOutFinalState(),
                    timeoutTickEvent, newArrayList(withMessage("because timedout")));
        }

        return transition;
    }

    @Override
    public Transition accept(Transition transition, RequestPinEvent requestPinEvent) {
        return nonHandledEvent(transition, requestPinEvent);
    }

    @Override
    public Transition accept(Transition transition, ConfirmPinEvent confirmPinEvent) {
        return nonHandledEvent(transition, confirmPinEvent);
    }

    @Override
    public Transition accept(Transition transition, RequestAcceptedEvent requestAcceptedEvent) {
        return nonHandledEvent(transition, requestAcceptedEvent);
    }
}
