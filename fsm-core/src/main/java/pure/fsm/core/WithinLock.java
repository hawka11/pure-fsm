package pure.fsm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.StateMachine.HandleEvent;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.StateMachine.STATE_MACHINE_INSTANCE;
import static pure.fsm.core.state.FinalState.DefaultFinalStates.ERROR_FINAL_STATE;

public class WithinLock {

    private final static Logger LOG = LoggerFactory.getLogger(WithinLock.class);

    public static Transition tryWithLock(String stateMachineId, StateMachineRepository repository, HandleEvent handleEvent) {
        return tryWithLock(stateMachineId, repository, handleEvent, 1, SECONDS);
    }

    /**
     * We only want one thread/event to be processed at a time for a given state machine,
     * this method should be used to synchronise any event handling within a single state machine.
     * <p>
     * This does not prevent multiple state machines being sent their own events concurrently
     */
    public static Transition tryWithLock(String stateMachineId, StateMachineRepository repository, HandleEvent handleEvent, long timeout, TimeUnit timeUnit) {
        Transition result = null;
        Optional<StateMachineRepository.Lock> lock = Optional.empty();

        try {
            lock = repository.tryLock(stateMachineId, timeout, timeUnit);
        } catch (Exception e) {
            LOG.error("Error with currentStateMachine [" + stateMachineId + "]", e);
        }

        if (lock.isPresent()) {
            final Transition last = lock.get().getLastTransition();

            try {
                result = handleEvent.handle(last, STATE_MACHINE_INSTANCE);

                Transition latestTransition =
                        last.setNextTransition(result);

                lock.get().update(latestTransition);

            } catch (Exception e) {
                LOG.error("Error with currentStateMachine [" + stateMachineId + "]", e);

                final Transition newTransition = Transition.To(ERROR_FINAL_STATE, null, last.getContext());

                lock.get().update(newTransition);

            } finally {
                lock.get().unlock();
            }
        } else {
            LOG.error("Could not get state machine lock for [{}]", stateMachineId);
        }

        return result;
    }
}
