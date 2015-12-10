package pure.fsm.example.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.FinalState;
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

public class BaseTelcoEventVisitor implements TelcoEventVisitor {

    private final static Logger LOG = LoggerFactory.getLogger(BaseTelcoEventVisitor.class);

    public final DistributedResourceFactory resourceFactory;

    protected BaseTelcoEventVisitor(DistributedResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public Transition handle(Transition last, TelcoEvent event) {
        return event.accept(last, this);
    }

    @Override
    public Transition visit(Transition prev, TimeoutTickEvent event) {
        System.out.println("In " + getClass().getSimpleName() + ", processing TimeoutTickEvent event ");

        Transition transition = Transition.To(prev.getState(), event, prev.getContext());
        if (!FinalState.class.isAssignableFrom(prev.getState().getClass())) {
            if (isTimeout(prev)) {
                transition = Transition.To(TIMEOUT_ERROR_FINAL_STATE,
                        event, prev.getContext().appendState(withMessage("timed-out")));
            }
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
        return LocalDateTime.now().isAfter(last.getTransitioned().plusMinutes(1));
    }

    private Transition nonHandledEvent(Transition last, TelcoEvent event) {
        LOG.warn("ignored event {}", event);
        return Transition.To(last.getState(), event, last.getContext());
    }
}
