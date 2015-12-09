package pure.fsm.core.test.fixture.event;

import pure.fsm.core.Transition;

public interface TelcoEventVisitor {
    Transition visit(Transition last, RequestRechargeEvent event);

    Transition visit(Transition last, CancelRechargeEvent event);

    Transition visit(Transition last, RechargeAcceptedEvent event);

    Transition visit(Transition last, TimeoutTickEvent event);
}
