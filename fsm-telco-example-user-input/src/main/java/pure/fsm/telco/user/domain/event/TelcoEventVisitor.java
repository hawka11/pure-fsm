package pure.fsm.telco.user.domain.event;

import pure.fsm.core.event.EventVisitor;
import pure.fsm.core.state.State;
import pure.fsm.telco.user.domain.TelcoRechargeContext;

public interface TelcoEventVisitor extends EventVisitor {
    State accept(TelcoRechargeContext context, RequestPinEvent requestPinEvent);

    State accept(TelcoRechargeContext context, ConfirmPinEvent confirmPinEvent);

    State accept(TelcoRechargeContext context, RequestAcceptedEvent requestAcceptedEvent);
}
