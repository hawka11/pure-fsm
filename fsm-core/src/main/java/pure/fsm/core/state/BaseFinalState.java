package pure.fsm.core.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.TimeoutTickEvent;

import static pure.fsm.core.context.UnlockContexts.unlockContexts;

public abstract class BaseFinalState implements FinalState {

    private final static Logger LOG = LoggerFactory.getLogger(BaseFinalState.class);

    @Override
    public boolean isTimeout(Transition prevTransition) {
        return false;
    }

    protected Transition nonHandledEvent(Context context, Event event) {
        LOG.trace("Final state [{}] received non handled event [{}], ignoring.",
                getClass().getName(), event.getClass().getName());
        return Transition.To(this, event, context);
    }

    @Override
    public Transition handle(Context context, Event event) {

        return nonHandledEvent(context, event);
    }

    @Override
    public Transition handle(Transition prevTransition, TimeoutTickEvent event) {

        return nonHandledEvent(prevTransition.getContext(), event);
    }

    @Override
    public void onExit(Context context, Event event) {
    }

    @Override
    public void onEntry(Context context, Event event, State prevState) {
        unlockContexts(context);
    }
}
