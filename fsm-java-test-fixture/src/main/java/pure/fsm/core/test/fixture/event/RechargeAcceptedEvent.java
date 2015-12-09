package pure.fsm.core.test.fixture.event;

import pure.fsm.core.Transition;

public class RechargeAcceptedEvent implements TelcoEvent {

    private final String acceptedPin;

    public RechargeAcceptedEvent(String acceptedPin) {
        this.acceptedPin = acceptedPin;
    }

    public String getAcceptedPin() {
        return acceptedPin;
    }

    @Override
    public Transition accept(Transition last, TelcoEventVisitor visitor) {
        return visitor.visit(last, this);
    }
}
