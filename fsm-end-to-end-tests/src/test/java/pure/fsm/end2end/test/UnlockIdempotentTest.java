package pure.fsm.end2end.test;

import org.junit.Test;
import pure.fsm.core.StateMachineRepository;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UnlockIdempotentTest extends BaseEnd2EndTest {

    public UnlockIdempotentTest(Supplier<StateMachineRepository> repository) {
        super(repository);
    }

    @Test
    public void unlockShouldBeIdempotent() {
        final StateMachineRepository repository = this.repository.get();

        StateMachineOperations ops = new StateMachineOperations(repository);
        final String stateMachineId = ops.createStateMachineInInitialState();

        final Optional<StateMachineRepository.Lock> lock = repository.tryLock(stateMachineId, 1, SECONDS);

        assertTrue(lock.get().unlock());
        assertFalse(lock.get().unlock());
    }

    @Test
    public void unlockAndRemoveShouldBeIdempotent() {
        final StateMachineRepository repository = this.repository.get();
        StateMachineOperations ops = new StateMachineOperations(repository);
        final String stateMachineId = ops.createStateMachineInInitialState();

        final Optional<StateMachineRepository.Lock> lock = repository.tryLock(stateMachineId, 1, SECONDS);

        assertTrue(lock.get().unlockAndRemove());
        assertFalse(lock.get().unlockAndRemove());
    }
}
