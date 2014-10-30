package pure.fsm.telco.user.domain.event;

import pure.fsm.core.event.Event;
import pure.fsm.core.state.State;
import pure.fsm.telco.user.domain.TelcoRechargeContext;

import static com.google.common.base.Objects.toStringHelper;

public class ConfirmPinEvent implements Event<TelcoRechargeContext, TelcoEventVisitor> {

    private final String pin;

    public ConfirmPinEvent(String pin) {
        super();
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    @Override
    public State accept(TelcoRechargeContext context, TelcoEventVisitor visitor) {
        return visitor.accept(context, this);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("pin", pin)
                .toString();
    }
}
