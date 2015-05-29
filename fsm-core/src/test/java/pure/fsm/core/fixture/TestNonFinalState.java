package pure.fsm.core.fixture;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.state.State;

public class TestNonFinalState implements State {
    @Override
    public Transition handle(Context context, Event event) {
        return null;
    }

    @Override
    public Transition handle(Transition prevTransition, TimeoutTickEvent event) {
        return null;
    }

    @Override
    public boolean isTimeout(Transition prevTransition) {
        return false;
    }

    @Override
    public void onExit(Context context, Event event) {

    }

    @Override
    public void onEntry(Context context, Event event, State prevState) {

    }
}
