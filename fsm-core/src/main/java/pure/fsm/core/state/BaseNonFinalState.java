package pure.fsm.core.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.event.Event;

import java.time.LocalDateTime;

public abstract class BaseNonFinalState implements State {

    private final static Logger LOG = LoggerFactory.getLogger(BaseNonFinalState.class);

    protected LocalDateTime getTimeoutDateTime(Context context) {
        //example timeout is 5 seconds
        return context.getTransitioned().plusSeconds(5);
    }

    @Override
    public boolean isTimeout(Context context) {

        return LocalDateTime.now().isAfter(getTimeoutDateTime(context));
    }

    protected State nonHandledEvent(Context context, Event event) {
        LOG.warn("State [{}] received non handled event [{}], ignoring.",
                getClass().getName(), event.getClass().getName());
        return this;
    }

    @Override
    public void onExit(Context context, Event event) {
    }

    @Override
    public void onEntry(Context context, Event event, State prevState) {
    }
}
