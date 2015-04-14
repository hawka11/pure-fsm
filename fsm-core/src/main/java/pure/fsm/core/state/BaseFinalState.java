package pure.fsm.core.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

import static pure.fsm.core.Transition.transition;
import static pure.fsm.core.context.UnlockTraits.unlockTraits;

public abstract class BaseFinalState implements FinalState {

    private final static Logger LOG = LoggerFactory.getLogger(BaseFinalState.class);

    @Override
    public StateFactory factory() {
        throw new IllegalStateException("In final state, probably shouldn't need to create another state");
    }

    @Override
    public boolean isTimeout(Context context) {
        return false;
    }

    protected Transition nonHandledEvent(Context context, Event event) {
        LOG.trace("Final state [{}] received non handled event [{}], ignoring.",
                getClass().getName(), event.getClass().getName());
        return transition(this, context);
    }

    @Override
    public Transition handle(Context context, Event event) {

        return nonHandledEvent(context, event);
    }

    @Override
    public void onExit(Context context, Event event) {
    }

    @Override
    public void onEntry(Context context, Event event, State prevState) {
        unlockTraits(context);
    }
}
