package simple.fsm.core.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;

public class TimedOutFinalState extends BaseFinalState {

    @Override
    public State handle(Context context, Event event) {
        throw new IllegalStateException("In Timed Out Final State, cannot process any more events");
    }
}
