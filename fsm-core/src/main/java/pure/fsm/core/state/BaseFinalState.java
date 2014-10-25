package pure.fsm.core.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.event.Event;

public abstract class BaseFinalState implements FinalState {

    private final static Logger LOG = LoggerFactory.getLogger(BaseFinalState.class);

    @Override
    public StateFactory factory() {
        throw new IllegalStateException("In final state, probably shouldn't need to create another state");
    }

    protected State nonHandledEvent(Context context, Event event) {
        LOG.trace("Final state [{}] received non handled event [{}], ignoring.",
                getClass().getName(), event.getClass().getName());
        return this;
    }

    @Override
    public State handle(Context context, Event event) {

        return nonHandledEvent(context, event);
    }

    @Override
    public void onExit(Context context, Event event) {
    }

    @Override
    public void onEntry(Context context, Event event, State prevState) {
        context.unlockResources();
    }
}
