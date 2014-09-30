package simple.fsm.optus.event;

import simple.fsm.core.Context;
import simple.fsm.core.event.EventVisitor;
import simple.fsm.core.state.State;

public interface OptusEventVisitor extends EventVisitor {
    State visit(Context context, RequestRechargeEvent requestRechargeEvent);

    State visit(Context context, CancelRechargeEvent cancelRechargeEvent);

    State visit(Context context, RechargeAcceptedEvent rechargeAcceptedEvent);
}
