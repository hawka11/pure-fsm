package simple.fsm.telco.event;

import simple.fsm.core.event.EventVisitor;
import simple.fsm.core.state.State;
import simple.fsm.telco.TelcoRechargeContext;

public interface TelcoEventVisitor extends EventVisitor {
    State visit(TelcoRechargeContext context, RequestRechargeEvent requestRechargeEvent);

    State visit(TelcoRechargeContext context, CancelRechargeEvent cancelRechargeEvent);

    State visit(TelcoRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent);
}
