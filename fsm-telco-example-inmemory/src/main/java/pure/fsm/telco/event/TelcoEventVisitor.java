package pure.fsm.telco.event;

import pure.fsm.core.Transition;
import pure.fsm.core.event.EventVisitor;

public interface TelcoEventVisitor extends EventVisitor {
    Transition visit(Transition transition, RequestRechargeEvent requestRechargeEvent);

    Transition visit(Transition transition, CancelRechargeEvent cancelRechargeEvent);

    Transition visit(Transition transition, RechargeAcceptedEvent rechargeAcceptedEvent);
}
