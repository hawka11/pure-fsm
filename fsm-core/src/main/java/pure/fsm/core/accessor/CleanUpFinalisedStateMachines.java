package pure.fsm.core.accessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.state.FinalState;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.trait.InitialContext.initialContext;

public class CleanUpFinalisedStateMachines {

    private final static Logger LOG = LoggerFactory.getLogger(CleanUpFinalisedStateMachines.class);

    private final StateMachineContextAccessor accessor;
    private final Collection<OnCleanupListener> cleanupListeners;
    private final long scheduleFrequency;
    private final TimeUnit scheduleTimeUnit;
    private final long keepFinalised;
    private final ChronoUnit keepFinalisedTimeUnit;
    private final ScheduledExecutorService scheduledExecutorService;

    public CleanUpFinalisedStateMachines(StateMachineContextAccessor accessor,
                                         Collection<OnCleanupListener> cleanupListeners,
                                         long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                         long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {
        this.accessor = accessor;
        this.cleanupListeners = cleanupListeners;
        this.scheduleFrequency = scheduleFrequency;
        this.scheduleTimeUnit = scheduleTimeUnit;
        this.keepFinalised = keepFinalised;
        this.keepFinalisedTimeUnit = keepFinalisedTimeUnit;

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
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

        accessor.getAllIds().forEach(id -> {
            Transition transition = accessor.get(id);
            if (transition.getState() instanceof FinalState) {
                try {
                    Optional<StateMachineContextAccessor.Lock> lock = accessor.tryLock(id, 1, SECONDS);
                    if (lock.isPresent() && shouldCleanup(lock.get().getTransition())) {
                        LOG.info("unlocking and removing state machine [{}]",
                                initialContext(lock.get().getTransition()).stateMachineId);

                        cleanupListeners.forEach(l -> l.onCleanup(transition));

                        lock.get().unlockAndRemove();
                    }
                } catch (Exception e) {
                    LOG.error("Error with sm [" + id + "]", e);
                }
            }
        });
    }

    protected boolean shouldCleanup(Transition transition) {

        return transition.getTransitioned().plus(keepFinalised, keepFinalisedTimeUnit).isBefore(LocalDateTime.now());
    }
}
