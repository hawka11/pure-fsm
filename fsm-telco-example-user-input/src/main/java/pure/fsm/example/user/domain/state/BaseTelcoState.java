package pure.fsm.example.user.domain.state;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.state.BaseNonFinalState;
import pure.fsm.core.state.TimedOutFinalState;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.example.user.domain.event.ConfirmPinEvent;
import pure.fsm.example.user.domain.event.RequestAcceptedEvent;
import pure.fsm.example.user.domain.event.RequestPinEvent;
import pure.fsm.example.user.domain.event.TelcoEventVisitor;

import static pure.fsm.core.context.ContextMessage.withMessage;

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
    public Transition handle(Transition prevTransition, TimeoutTickEvent event) {
        return event.accept(prevTransition, this);
    }

    @Override
    public Transition visit(Transition prevTransition, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        Transition transition = Transition.To(this, timeoutTickEvent, prevTransition.getContext());
        if (isTimeout(prevTransition)) {
            transition = Transition.To(new TimedOutFinalState(),
                    timeoutTickEvent, prevTransition.getContext().appendState(withMessage("because timedout")));
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
