package pure.fsm.core.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.accessor.StateMachineContextAccessor;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.StateMachine.STATE_MACHINE_INSTANCE;

public class StateMachineTemplate {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineTemplate.class);

    private final StateMachineContextAccessor accessor;

    public StateMachineTemplate(StateMachineContextAccessor accessor) {
        this.accessor = accessor;
    }

    public Transition get(String stateMachineId) {
        return accessor.get(stateMachineId);
    }

    public Set<String> getAllIds() {
        return accessor.getAllIds();
    }

    public String create(State initialState, Class<? extends StateFactory> stateFactory, List<Context> initialContexts) {
        return accessor.create(initialState, stateFactory, initialContexts);
    }

    public void tryWithLock(String stateMachineId, StateMachineCallback stateMachineCallback) {

        tryWithLock(stateMachineId, stateMachineCallback, 1, SECONDS);
    }

    /**
     * We only want one thread/event to be processed at a time for a given state machine,
     * this method should be used to synchronise any event handling within a single state machine.
     * <p>
     * This does not prevent multiple state machines being sent their own events concurrently
     */
    public void tryWithLock(String stateMachineId, StateMachineCallback stateMachineCallback, long timeout, TimeUnit timeUnit) {
        Optional<StateMachineContextAccessor.Lock> lock = Optional.empty();

        try {
            lock = accessor.tryLock(stateMachineId, timeout, timeUnit);
        } catch (Exception e) {
            LOG.error("Error with currentStateMachine [{}]", stateMachineId);
            stateMachineCallback.onLockFailed(e);
        }

        if (lock.isPresent()) {
            try {
                Transition newTransition = stateMachineCallback.doWith(lock.get().getTransition(), STATE_MACHINE_INSTANCE);
                lock.get().update(newTransition);
            } catch (Exception e) {
                LOG.error("Error with currentStateMachine [" + stateMachineId + "]", e);

                Transition newTransition = stateMachineCallback.onError(lock.get().getTransition(), STATE_MACHINE_INSTANCE, e);

                lock.get().update(newTransition);
            } finally {
                lock.get().unlock();
            }
        } else {
            LOG.error("Could not get state machine lock for [{}]", stateMachineId);
        }
    }
}
