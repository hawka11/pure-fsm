package pure.fsm.telco.event;

import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

public class CancelRechargeEvent implements Event<TelcoEventVisitor> {

    @Override
    public Transition accept(Transition transition, TelcoEventVisitor visitor) {
        return visitor.visit(transition, this);
    }
}
