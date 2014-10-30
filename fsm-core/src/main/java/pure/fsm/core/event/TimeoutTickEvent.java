package pure.fsm.core.event;

import pure.fsm.core.Context;
import pure.fsm.core.state.State;

public class TimeoutTickEvent implements Event {

    @Override
    public State accept(Context context, EventVisitor visitor) {
        return visitor.visit(context, this);
    }

    @Override
    public String toString() {
        return "TimeoutTickEvent";
    }
}
