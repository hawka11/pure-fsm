package pure.fsm.telco.user.domain.event;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.EventVisitor;

public interface TelcoEventVisitor extends EventVisitor {
    Transition accept(Context context, RequestPinEvent requestPinEvent);

    Transition accept(Context context, ConfirmPinEvent confirmPinEvent);

    Transition accept(Context context, RequestAcceptedEvent requestAcceptedEvent);
}
