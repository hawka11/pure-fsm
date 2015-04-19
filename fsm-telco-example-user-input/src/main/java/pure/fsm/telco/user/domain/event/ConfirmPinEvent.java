package pure.fsm.telco.user.domain.event;

import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

import static com.google.common.base.Objects.toStringHelper;

public class ConfirmPinEvent implements Event<TelcoEventVisitor> {

    private final String pin;

    public ConfirmPinEvent(String pin) {
        super();
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    @Override
    public Transition accept(Transition transition, TelcoEventVisitor visitor) {
        return visitor.accept(transition, this);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("pin", pin)
                .toString();
    }
}
