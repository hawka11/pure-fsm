package pure.fsm.telco.event;

import pure.fsm.core.event.EventVisitor;
import pure.fsm.core.state.State;
import pure.fsm.telco.TelcoRechargeContext;

public interface TelcoEventVisitor extends EventVisitor {
    State visit(TelcoRechargeContext context, RequestRechargeEvent requestRechargeEvent);

    State visit(TelcoRechargeContext context, CancelRechargeEvent cancelRechargeEvent);

    State visit(TelcoRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent);
}
