package pure.fsm.core.cleanup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pure.fsm.core.StateMachineRepository;
import pure.fsm.core.StateMachineRepository.Lock;
import pure.fsm.core.Transition;
import pure.fsm.core.fixture.TestEvent;
import pure.fsm.core.fixture.TestFinalState;
import pure.fsm.core.fixture.TestNonFinalState;

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
        cleaner.cleanupIfFinalState("1", createTransitionInState(new TestNonFinalState()));

        verify(repository, never()).tryLock(eq("1"), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void shouldAttemptCleanupWhenFinalState() {
        final Transition transition = createTransitionInState(new TestFinalState());

        when(repository.tryLock("2", 1, SECONDS)).thenReturn(Optional.of(lock));
        when(lock.getLastTransition()).thenReturn(transition);

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
        return Transition.To(state, new TestEvent(), initialContext("1", newArrayList()));
    }
}