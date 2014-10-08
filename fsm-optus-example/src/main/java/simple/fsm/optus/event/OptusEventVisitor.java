package simple.fsm.optus.event;

import simple.fsm.core.event.EventVisitor;
import simple.fsm.core.state.State;
import simple.fsm.optus.OptusRechargeContext;

public interface OptusEventVisitor extends EventVisitor {
    State visit(OptusRechargeContext context, RequestRechargeEvent requestRechargeEvent);

    State visit(OptusRechargeContext context, CancelRechargeEvent cancelRechargeEvent);

    State visit(OptusRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent);
}
