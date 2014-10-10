package simple.fsm.core.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;

public class SuccessFinalState extends BaseFinalState {

    @Override
    public State handle(Context context, Event event) {

        throw new IllegalStateException("In SuccessFinalState, cannot process any more events");
    }
}
