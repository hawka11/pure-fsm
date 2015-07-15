package pure.fsm.end2end.event;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

public class CancelRechargeEvent implements Event<TelcoEventVisitor> {

    @Override
    public Transition accept(Context context, TelcoEventVisitor visitor) {
        return visitor.visit(context, this);
    }
}
