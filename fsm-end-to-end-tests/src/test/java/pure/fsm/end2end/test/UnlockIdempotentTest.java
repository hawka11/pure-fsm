package pure.fsm.end2end.test;

import org.junit.Test;
import pure.fsm.core.TransitionRepository;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UnlockIdempotentTest extends BaseEnd2EndTest {

    public UnlockIdempotentTest(Supplier<TransitionRepository> repository) {
        super(repository);
    }

    @Test
    public void unlockShouldBeIdempotent() {
        final TransitionRepository repository = this.repository.get();

        StateMachineOperations ops = new StateMachineOperations(repository);
        final String stateMachineId = ops.createStateMachineInInitialState();

        final Optional<TransitionRepository.Lock> lock = repository.tryLock(stateMachineId, 1, SECONDS);

        assertTrue(lock.get().unlock());
        assertFalse(lock.get().unlock());
    }

    @Test
    public void unlockAndRemoveShouldBeIdempotent() {
        final TransitionRepository repository = this.repository.get();
        StateMachineOperations ops = new StateMachineOperations(repository);
        final String stateMachineId = ops.createStateMachineInInitialState();

        final Optional<TransitionRepository.Lock> lock = repository.tryLock(stateMachineId, 1, SECONDS);

        assertTrue(lock.get().unlockAndRemove());
        assertFalse(lock.get().unlockAndRemove());
    }
}
