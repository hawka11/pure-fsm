package pure.fsm.core.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

import java.time.LocalDateTime;

public abstract class BaseNonFinalState implements State {

    private final static Logger LOG = LoggerFactory.getLogger(BaseNonFinalState.class);

    protected LocalDateTime getTimeoutDateTime(Transition prevTransition) {
        //example timeout is 5 seconds
        return prevTransition.getTransitioned().plusSeconds(5);
    }

    @Override
    public boolean isTimeout(Transition prevTransition) {

        return LocalDateTime.now().isAfter(getTimeoutDateTime(prevTransition));
    }

    protected Transition nonHandledEvent(Transition lastTransition, Event event) {
        LOG.warn("State [{}] received non handled event [{}], ignoring.",
                getClass().getName(), event.getClass().getName());
        return lastTransition.transitionTo(this, event);
    }

    @Override
    public void onExit(Transition newTransition, Event event) {
    }

    @Override
    public void onEntry(Transition newTransition, Event event, State prevState) {
    }
}
