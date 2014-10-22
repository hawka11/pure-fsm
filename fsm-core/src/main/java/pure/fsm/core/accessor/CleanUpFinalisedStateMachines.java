package pure.fsm.core.accessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.StateMachine;
import pure.fsm.core.state.FinalState;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class CleanUpFinalisedStateMachines {

    private final static Logger LOG = LoggerFactory.getLogger(CleanUpFinalisedStateMachines.class);

    private final StateMachineAccessor accessor;
    private final long cleanupTimeout;
    private final ChronoUnit timeUnit;
    private final ScheduledExecutorService scheduledExecutorService;

    public CleanUpFinalisedStateMachines(StateMachineAccessor accessor, long cleanupTimeout, ChronoUnit timeUnit) {
        this.accessor = accessor;
        this.cleanupTimeout = cleanupTimeout;
        this.timeUnit = timeUnit;

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startScheduler() {
        scheduledExecutorService.scheduleWithFixedDelay(
                this::checkForFinalizedStateMachinesAndCleanupIfRequired,
                0, cleanupTimeout,
                TimeUnit.SECONDS);
    }

    public void stopScheduler() {
        scheduledExecutorService.shutdown();
    }

    public void checkForFinalizedStateMachinesAndCleanupIfRequired() {
        LOG.info("About to check for outdated finalized state machines.");

        accessor.getAllIds().forEach(id -> {
            StateMachine stateMachine = accessor.get(id);
            if (stateMachine.getCurrentState() instanceof FinalState) {
                try {
                    Optional<StateMachineAccessor.Lock> lock = accessor.tryLock(id, 1, SECONDS);
                    if (lock.isPresent() && shouldCleanup(lock.get().getStateMachine())) {
                        LOG.info("unlocking and removing state machine [{}]",
                                lock.get().getStateMachine().getStateMachineId());

                        lock.get().unlockAndRemove();
                    }
                } catch (Exception e) {
                    LOG.error("Error with sm [" + id + "]", e);
                }
            }
        });
    }

    protected boolean shouldCleanup(StateMachine stateMachine) {

        return stateMachine.getContext().getTransitioned().plus(cleanupTimeout, timeUnit).isBefore(LocalDateTime.now());
    }
}
