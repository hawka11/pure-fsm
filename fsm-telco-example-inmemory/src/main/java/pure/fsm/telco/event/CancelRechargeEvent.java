package pure.fsm.telco.event;

import pure.fsm.core.event.Event;
import pure.fsm.core.state.State;
import pure.fsm.telco.TelcoRechargeContext;

public class CancelRechargeEvent implements Event<TelcoRechargeContext, TelcoEventVisitor> {

    @Override
    public State accept(TelcoRechargeContext context, TelcoEventVisitor visitor) {
        return visitor.visit(context, this);
    }
}
