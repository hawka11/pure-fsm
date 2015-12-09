package pure.fsm.example.user.domain.event;

import pure.fsm.core.Transition;

import static com.google.common.base.Objects.toStringHelper;

public class ConfirmPinEvent implements TelcoEvent {

    private final String pin;

    public ConfirmPinEvent(String pin) {
        super();
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    @Override
    public Transition accept(Transition last, TelcoEventVisitor visitor) {
        return visitor.visit(last, this);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("pin", pin)
                .toString();
    }
}
