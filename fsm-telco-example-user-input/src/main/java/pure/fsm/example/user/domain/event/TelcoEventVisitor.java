package pure.fsm.example.user.domain.event;

import pure.fsm.core.Transition;

public interface TelcoEventVisitor {
    Transition visit(Transition last, RequestAcceptedEvent event);

    Transition visit(Transition last, ConfirmPinEvent event);

    Transition visit(Transition last, RequestPinEvent event);

    Transition visit(Transition last, TimeoutTickEvent event);
}
