package pure.fsm.telco.event;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

public class RechargeAcceptedEvent implements Event<TelcoEventVisitor> {

    private final String acceptedPin;

    public RechargeAcceptedEvent(String acceptedPin) {
        this.acceptedPin = acceptedPin;
    }

    public String getAcceptedPin() {
        return acceptedPin;
    }

    @Override
    public Transition accept(Context context, TelcoEventVisitor visitor) {
        return visitor.visit(context, this);
    }
}
