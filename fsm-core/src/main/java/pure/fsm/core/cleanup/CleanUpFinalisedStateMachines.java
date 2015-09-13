package pure.fsm.core.cleanup;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.repository.StateMachineRepository;
import pure.fsm.core.repository.StateMachineRepository.Lock;
import pure.fsm.core.state.FinalState;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class CleanUpFinalisedStateMachines {

    private final static Logger LOG = LoggerFactory.getLogger(CleanUpFinalisedStateMachines.class);

    private final StateMachineRepository repository;
    private final Collection<OnCleanupListener> cleanupListeners;
    private final long scheduleFrequency;
    private final TimeUnit scheduleTimeUnit;
    private final long keepFinalised;
    private final ChronoUnit keepFinalisedTimeUnit;
    private final ScheduledExecutorService scheduledExecutorService;

    public CleanUpFinalisedStateMachines(StateMachineRepository repository,
                                         Collection<OnCleanupListener> cleanupListeners,
                                         long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                         long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {
        this.repository = repository;
        this.cleanupListeners = cleanupListeners;
        this.scheduleFrequency = scheduleFrequency;
        this.scheduleTimeUnit = scheduleTimeUnit;
        this.keepFinalised = keepFinalised;
        this.keepFinalisedTimeUnit = keepFinalisedTimeUnit;

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder().namingPattern("pure-fsm-cleaner").build());
    }

    public void startScheduler() {
        scheduledExecutorService.scheduleWithFixedDelay(
                this::checkForFinalizedStateMachinesAndCleanupIfRequired,
                0, scheduleFrequency,
                scheduleTimeUnit);
    }

    public void stopScheduler() {
        scheduledExecutorService.shutdown();
    }

    public void checkForFinalizedStateMachinesAndCleanupIfRequired() {
        LOG.info("About to check for outdated finalized state machines.");

        try {

            repository.getAllIds().forEach(id -> {
                try {
                    final Transition transition = repository.get(id);
                    cleanupIfFinalState(id, transition);
                } catch (Exception e) {
                    LOG.warn(format("Something went bad with id [%s]", id), e);
                }
            });

        } catch (Exception e) {
            LOG.warn("Something went bad", e);
        }
    }

    @VisibleForTesting
    void cleanupIfFinalState(String id, Transition transition) {
        if (transition.getState() instanceof FinalState) {

            Optional<Lock> lock = repository.tryLock(id, 1, SECONDS);

            try {

                lock.ifPresent(l -> cleanupIfFinalizedTimeHasExpired(transition, l));

            } catch (Exception e) {

                LOG.error("Error with sm [" + id + "]", e);
            } finally {

                lock.ifPresent(Lock::unlock);
            }
        }
    }

    private void cleanupIfFinalizedTimeHasExpired(Transition transition, Lock lock) {
        final Transition latestTransition = lock.getLatestTransition();

        if (shouldCleanup(latestTransition)) {
            try {
                cleanupListeners.forEach(l -> l.onCleanup(transition));

                LOG.info("unlocking and removing state machine [{}]", stateMachineId(latestTransition));
            } finally {
                lock.unlockAndRemove();
            }
        }
    }

    private String stateMachineId(Transition latestTransition) {
        return (latestTransition.getContext() != null)
                ? latestTransition.getContext().stateMachineId()
                : "";
    }

    protected boolean shouldCleanup(Transition transition) {

        return transition.getTransitioned().plus(keepFinalised, keepFinalisedTimeUnit).isBefore(LocalDateTime.now());
    }
}
