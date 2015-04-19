package pure.fsm.core.event;

import pure.fsm.core.Transition;

public class TimeoutTickEvent implements Event {

    @Override
    public Transition accept(Transition transition, EventVisitor visitor) {
        return visitor.visit(transition, this);
    }

    @Override
    public String toString() {
        return "TimeoutTickEvent";
    }
}
