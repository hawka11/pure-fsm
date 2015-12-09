package pure.fsm.example.inmemory.event;

import pure.fsm.core.Transition;

public class CancelRechargeEvent implements TelcoEvent {

    @Override
    public Transition accept(Transition last, TelcoEventVisitor visitor) {
        return visitor.visit(last, this);
    }
}
