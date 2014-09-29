package simple.fsm.state;

import simple.fsm.Context;
import simple.fsm.event.Event;

public class ErrorFinalState implements FinalState {
    @Override
    public State handle(Context context, Event event) {
        return this;
    }
}
