package pure.fsm.core.event;

import pure.fsm.core.state.State;
import pure.fsm.core.Context;

public class TimeoutTickEvent implements Event {

    @Override
    public State accept(Context context, EventVisitor visitor) {
        return visitor.visit(context, this);
    }
}
