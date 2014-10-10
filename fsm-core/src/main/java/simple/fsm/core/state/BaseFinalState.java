package simple.fsm.core.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;

public abstract class BaseFinalState implements FinalState {

    @Override
    public StateFactory factory() {
        throw new IllegalStateException("In final state, probably shouldn't need to create another state");
    }

    @Override
    public void onExit(Context context, Event event) {
    }

    @Override
    public void onEntry(Context context, Event event, State prevState) {
        context.unlockResources();
    }
}
