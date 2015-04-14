package pure.fsm.telco.user.domain.event;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

import java.util.List;

import static com.google.common.base.Objects.toStringHelper;

public class RequestAcceptedEvent implements Event<TelcoEventVisitor> {

    private final List<String> pins;

    public RequestAcceptedEvent(List<String> pins) {
        this.pins = pins;
    }

    public List<String> getPins() {
        return pins;
    }

    @Override
    public Transition accept(Context context, TelcoEventVisitor visitor) {
        return visitor.accept(context, this);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("pins", pins)
                .toString();
    }
}
