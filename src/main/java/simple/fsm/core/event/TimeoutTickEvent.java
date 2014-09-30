package simple.fsm.core.event;

import simple.fsm.core.Context;
import simple.fsm.core.state.State;

public class TimeoutTickEvent implements Event {

    @Override
    public State accept(Context context, EventVisitor visitor) {
        return visitor.visit(context, this);
    }
}
