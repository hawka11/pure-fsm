package pure.fsm.telco.event;

import pure.fsm.core.event.Event;
import pure.fsm.core.state.State;
import pure.fsm.telco.TelcoRechargeContext;

public class RechargeAcceptedEvent implements Event<TelcoRechargeContext, TelcoEventVisitor> {

    private final String acceptedPin;

    public RechargeAcceptedEvent(String acceptedPin) {
        this.acceptedPin = acceptedPin;
    }

    public String getAcceptedPin() {
        return acceptedPin;
    }

    @Override
    public State accept(TelcoRechargeContext context, TelcoEventVisitor visitor) {
        return visitor.visit(context, this);
    }
}
