package pure.fsm.core.cleanup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pure.fsm.core.StateMachineRepository;
import pure.fsm.core.StateMachineRepository.Lock;
import pure.fsm.core.Transition;
import pure.fsm.core.fixture.TestEvent.RechargeEvent;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pure.fsm.core.Context.initialContext;
import static pure.fsm.core.fixture.TestState.RECHARGE_ACCEPTED_FINAL_STATE;
import static pure.fsm.core.fixture.TestState.RECHARGE_REQUESTED_STATE;

@RunWith(MockitoJUnitRunner.class)
public class CleanUpFinalisedStateMachinesTest {

    @Mock
    private StateMachineRepository repository;

    @Mock
    private Lock lock;

    private CleanUpFinalisedStateMachines cleaner;

    @Before
    public void beforeEach() {
        cleaner = new CleanUpFinalisedStateMachines(repository, newArrayList(), 1, SECONDS, 1, MILLIS);
    }

    @Test
    public void shouldNotAttemptCleanupWhenNonFinalState() {
        cleaner.cleanupIfFinalState("1", createTransitionInState(RECHARGE_REQUESTED_STATE));

        verify(repository, never()).tryLock(eq("1"), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void shouldAttemptCleanupWhenFinalState() {
        final Transition transition = createTransitionInState(RECHARGE_ACCEPTED_FINAL_STATE);

        when(repository.tryLock("2", 1, SECONDS)).thenReturn(Optional.of(lock));
        when(lock.getLast()).thenReturn(transition);

        cleaner.cleanupIfFinalState("2", transition);

        verify(repository).tryLock(eq("2"), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void shouldFORCECleanupIfCannotGetTransitionMostLikelyFromJsonStructureChange() {
        when(repository.get("2")).thenThrow(new IllegalStateException(""));
        when(repository.tryLock("2", 1, SECONDS)).thenReturn(Optional.of(lock));

        cleaner.processStateMachineId("2");

        verify(lock).unlockAndRemove();
    }

    private Transition createTransitionInState(Object state) {
        return Transition.To(state, new RechargeEvent(), initialContext("1", newArrayList()));
    }
}