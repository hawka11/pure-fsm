package pure.fsm.telco.user.domain.event;

import pure.fsm.core.event.Event;
import pure.fsm.core.state.State;
import pure.fsm.telco.user.domain.TelcoRechargeContext;

import java.util.List;

public class RequestPinEvent implements Event<TelcoRechargeContext, TelcoEventVisitor> {

    private final List<String> pins;

    public RequestPinEvent(List<String> pins) {
        this.pins = pins;
    }

    public List<String> getPins() {
        return pins;
    }

    @Override
    public State accept(TelcoRechargeContext context, TelcoEventVisitor visitor) {
        return visitor.accept(context, this);
    }
}
