package simple.fsm.optus.event;

import simple.fsm.core.event.Event;
import simple.fsm.core.state.State;
import simple.fsm.optus.OptusRechargeContext;

public class RechargeAcceptedEvent implements Event<OptusRechargeContext, OptusEventVisitor> {

    @Override
    public State accept(OptusRechargeContext context, OptusEventVisitor visitor) {
        return visitor.visit(context, this);
    }
}
