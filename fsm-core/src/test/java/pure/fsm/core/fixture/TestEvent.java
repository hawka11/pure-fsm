package pure.fsm.core.fixture;

import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.EventVisitor;

public class TestEvent implements Event {

    @Override
    public Transition accept(Transition transition, EventVisitor visitor) {
        return null;
    }
}
