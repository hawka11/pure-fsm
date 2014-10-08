package simple.fsm.core.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;

import java.time.LocalDateTime;

public class ErrorFinalState extends BaseFinalState {

    @Override
    public State handle(Context context, Event event) {

        throw new IllegalStateException("In Error Final State, cannot process any more events");
    }
}
