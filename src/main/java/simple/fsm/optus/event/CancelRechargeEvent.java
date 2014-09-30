package simple.fsm.optus.event;

import simple.fsm.core.Context;
import simple.fsm.core.event.BaseEvent;
import simple.fsm.core.state.State;

public class CancelRechargeEvent extends BaseEvent<OptusEventVisitor> {

    protected CancelRechargeEvent(Context context) {
        super(context);
    }

    @Override
    public State accept(OptusEventVisitor visitor) {
        return visitor.visit(context, this);
    }
}
