package pure.fsm.core.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.accessor.StateMachineAccessor;
import pure.fsm.core.StateMachine;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class StateMachineTemplate {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineTemplate.class);

    private final StateMachineAccessor accessor;

    public StateMachineTemplate(StateMachineAccessor accessor) {
        this.accessor = accessor;
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
        Optional<StateMachineAccessor.Lock> lock = Optional.empty();

        try {
            lock = accessor.tryLock(stateMachineId, timeout, timeUnit);
        } catch (Exception e) {
            LOG.error("Error with currentStateMachine [{}]", stateMachineId);
            stateMachineCallback.onLockFailed(e);
        }

        if (lock.isPresent()) {
            try {
                StateMachine newStateMachine = stateMachineCallback.doWith(lock.get().getStateMachine());
                lock.get().update(newStateMachine);
            } catch (Exception e) {
                LOG.error("Error with currentStateMachine [" + stateMachineId + "]", e);
                StateMachine newStateMachine = stateMachineCallback.onError(lock.get().getStateMachine(), e);
                lock.get().update(newStateMachine);
            } finally {
                lock.get().unlock();
            }
        } else {
            LOG.error("Could not get state machine lock for [{}]", stateMachineId);
        }
    }
}
