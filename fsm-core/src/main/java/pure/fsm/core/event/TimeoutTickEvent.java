package pure.fsm.core.event;

import org.apache.commons.lang3.NotImplementedException;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;

public class TimeoutTickEvent implements Event {

    @Override
    public Transition accept(Context context, EventVisitor visitor) {
        throw new NotImplementedException("");
    }

    public Transition accept(Transition transition, EventVisitor visitor) {
        return visitor.visit(transition, this);
    }

    @Override
    public String toString() {
        return "TimeoutTickEvent";
    }
}
