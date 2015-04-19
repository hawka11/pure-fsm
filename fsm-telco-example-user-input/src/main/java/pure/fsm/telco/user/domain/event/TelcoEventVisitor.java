package pure.fsm.telco.user.domain.event;

import pure.fsm.core.Transition;
import pure.fsm.core.event.EventVisitor;

public interface TelcoEventVisitor extends EventVisitor {
    Transition accept(Transition transition, RequestPinEvent requestPinEvent);

    Transition accept(Transition transition, ConfirmPinEvent confirmPinEvent);

    Transition accept(Transition transition, RequestAcceptedEvent requestAcceptedEvent);
}
