package pure.fsm.example.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.example.user.domain.event.ConfirmPinEvent;
import pure.fsm.example.user.domain.event.RequestAcceptedEvent;
import pure.fsm.example.user.domain.event.RequestPinEvent;
import pure.fsm.example.user.domain.event.TelcoEvent;
import pure.fsm.example.user.domain.event.TelcoEventVisitor;
import pure.fsm.example.user.domain.event.TimeoutTickEvent;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;

import java.time.LocalDateTime;

import static pure.fsm.core.FinalState.TIMEOUT_ERROR_FINAL_STATE;
import static pure.fsm.core.context.ContextMessage.withMessage;

public class BaseTelcoState implements TelcoEventVisitor {

    private final static Logger LOG = LoggerFactory.getLogger(BaseTelcoState.class);

    private final DistributedResourceFactory resourceFactory;

    protected BaseTelcoState(DistributedResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public DistributedResourceFactory resourceFactory() {
        return resourceFactory;
    }

    public Transition handle(Transition last, TelcoEvent event) {
        return event.accept(last, this);
    }

    @Override
    public Transition visit(Transition prevTransition, TimeoutTickEvent timeoutTickEvent) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        Transition transition = Transition.To(this, timeoutTickEvent, prevTransition.getContext());
        if (isTimeout(prevTransition)) {
            transition = Transition.To(TIMEOUT_ERROR_FINAL_STATE,
                    timeoutTickEvent, prevTransition.getContext().appendState(withMessage("because timed-out")));
        }

        return transition;
    }

    @Override
    public Transition visit(Transition last, RequestPinEvent event) {
        return nonHandledEvent(last, event);
    }

    @Override
    public Transition visit(Transition last, ConfirmPinEvent event) {
        return nonHandledEvent(last, event);
    }

    @Override
    public Transition visit(Transition last, RequestAcceptedEvent event) {
        return nonHandledEvent(last, event);
    }

    protected boolean isTimeout(Transition last) {
        return LocalDateTime.now().isAfter(last.getTransitioned().plusSeconds(5));
    }

    private Transition nonHandledEvent(Transition last, TelcoEvent event) {
        LOG.warn("ignored event {}", event);
        return Transition.To(last.getState(), event, last.getContext());
    }
}
