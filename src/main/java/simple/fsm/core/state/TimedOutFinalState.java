package simple.fsm.core.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;

public class TimedOutFinalState implements FinalState {

    private final String msg;

    public TimedOutFinalState(String msg) {
        this.msg = msg;
    }

    @Override
    public State handle(Context context, Event event) {
        throw new IllegalStateException("In Timed Out Final State, cannot process any more events");
    }

    @Override
    public void onExit(Context context, Event event) {

    }

    @Override
    public void onEntry(Context context, Event event, State prevState) {

    }
}
