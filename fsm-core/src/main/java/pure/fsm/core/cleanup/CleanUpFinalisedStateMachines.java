package pure.fsm.core.cleanup;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.StateMachineRepository;
import pure.fsm.core.StateMachineRepository.Lock;
import pure.fsm.core.FinalState;

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

            repository.getIds().forEach(this::processStateMachineId);

        } catch (Exception e) {
            LOG.warn("Something went bad", e);
        }
    }

    @VisibleForTesting
    void processStateMachineId(String id) {
        try {
            final Optional<Transition> transition = getTransition(id);
            if (transition.isPresent()) {
                cleanupIfFinalState(id, transition.get());
            } else {
                LOG.warn("Could not retrieve transition [{}], json format might of changed? Forcing cleanup!!", id);
                forceCleanup(repository, id);
            }
        } catch (Exception e) {
            LOG.warn(format("Retrieved transition [%s], but something else went wrong. Forcing cleanup!!", id), e);
            forceCleanup(repository, id);
        }
    }

    private Optional<Transition> getTransition(String id) {
        try {
            return Optional.ofNullable(repository.get(id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void forceCleanup(StateMachineRepository repository, String smId) {
        final Optional<Lock> lock = repository.tryLock(smId, 1, SECONDS);
        lock.ifPresent((lock1) -> {
            LOG.info("Got lock for [{}], removing from db", smId);
            lock1.unlockAndRemove();
        });
    }

    @VisibleForTesting
    void cleanupIfFinalState(String id, Transition transition) {
        if (transition.getState() instanceof FinalState) {

            Optional<Lock> lock = repository.tryLock(id, 1, SECONDS);

            try {

                lock.ifPresent(this::cleanupIfFinalizedTimeHasExpired);

            } finally {

                lock.ifPresent(Lock::unlock);
            }
        }
    }

    private void cleanupIfFinalizedTimeHasExpired(Lock lock) {
        final Transition latestTransition = lock.getLast();

        if (shouldCleanup(latestTransition)) {
            try {
                cleanupListeners.forEach(l -> l.onCleanup(latestTransition));

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
