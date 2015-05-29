package pure.fsm.telco.event;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.EventVisitor;

public interface TelcoEventVisitor extends EventVisitor {
    Transition visit(Context context, RequestRechargeEvent requestRechargeEvent);

    Transition visit(Context context, CancelRechargeEvent cancelRechargeEvent);

    Transition visit(Context context, RechargeAcceptedEvent rechargeAcceptedEvent);
}
