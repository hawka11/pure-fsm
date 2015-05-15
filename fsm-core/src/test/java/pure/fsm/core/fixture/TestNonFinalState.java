package pure.fsm.core.fixture;

import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.state.State;

public class TestNonFinalState implements State {
    @Override
    public Transition handle(Transition prevTransition, Event event) {
        return null;
    }

    @Override
    public boolean isTimeout(Transition prevTransition) {
        return false;
    }

    @Override
    public void onExit(Transition newTransition, Event event) {

    }

    @Override
    public void onEntry(Transition newTransition, Event event, State prevState) {

    }
}
