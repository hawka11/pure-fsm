package pure.fsm.core.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

import static pure.fsm.core.context.UnlockContexts.unlockContexts;

public abstract class BaseFinalState implements FinalState {

    private final static Logger LOG = LoggerFactory.getLogger(BaseFinalState.class);

    @Override
    public boolean isTimeout(Transition prevTransition) {
        return false;
    }

    protected Transition nonHandledEvent(Transition transition, Event event) {
        LOG.trace("Final state [{}] received non handled event [{}], ignoring.",
                getClass().getName(), event.getClass().getName());
        return transition.transitionTo(this, event);
    }

    @Override
    public Transition handle(Transition prevTransition, Event event) {

        return nonHandledEvent(prevTransition, event);
    }

    @Override
    public void onExit(Transition prevTransition, Event event) {
    }

    @Override
    public void onEntry(Transition prevTransition, Event event, State prevState) {
        unlockContexts(prevTransition);
    }
}
