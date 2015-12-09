package pure.fsm.example.user.domain.event;

import pure.fsm.core.Transition;

import java.util.List;

import static com.google.common.base.Objects.toStringHelper;

public class RequestPinEvent implements TelcoEvent {

    private final List<String> pins;

    public RequestPinEvent(List<String> pins) {
        this.pins = pins;
    }

    public List<String> getPins() {
        return pins;
    }

    @Override
    public Transition accept(Transition last, TelcoEventVisitor visitor) {
        return visitor.visit(last, this);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("pins", pins)
                .toString();
    }
}
