package pure.fsm.jdbi.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import pure.fsm.core.StateFactoryRegistration;
import pure.fsm.core.Transition;
import pure.fsm.core.fixture.TestEvent;
import pure.fsm.core.fixture.TestFinalState;
import pure.fsm.core.fixture.TestNonFinalState;
import pure.fsm.core.fixture.TestStateFactory;
import pure.fsm.core.repository.StateMachineRepository.Lock;

import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JdbiStateMachineRepositoryTest {

    protected static JdbiRule JDBI_RULE = new JdbiRule("purefsm-test.yml");

    protected static FlywayRule FLYWAY_RULE = new FlywayRule(
            "fsm-repository-jdbi/src/main/database/flyway-conf/flyway.fsm.properties",
            "fsm-repository-jdbi/src/main/database/flyway-sql", () -> JDBI_RULE.DATA_SOURCE);
    @Rule
    public RuleChain chain = RuleChain.emptyRuleChain()
            .around(JDBI_RULE)
            .around(FLYWAY_RULE);

    private JdbiStateMachineRepository repository;

    @Before
    public void beforeEach() {
        StateFactoryRegistration.registerStateFactory(new TestStateFactory());
        repository = new JdbiStateMachineRepository(JDBI_RULE.DBI);
    }

    @Test
    public void shouldOnlyRetrieveInProgressIds() throws Exception {
        final String smIdOne = repository.create(new TestNonFinalState(), TestStateFactory.class, newArrayList());
        final String smIdTwo = repository.create(new TestNonFinalState(), TestStateFactory.class, newArrayList());
        final String smIdThree = repository.create(new TestNonFinalState(), TestStateFactory.class, newArrayList());
        final String smIdFour = repository.create(new TestNonFinalState(), TestStateFactory.class, newArrayList());

        repository.tryLock(smIdTwo, 1, SECONDS).get().update(transitionToFinalState(smIdTwo));
        repository.tryLock(smIdThree, 1, SECONDS).get().update(transitionToFinalState(smIdThree));

        final Set<String> inProgressIds = repository.getInProgressIds();

        assertThat(inProgressIds).containsExactly(smIdOne, smIdFour);
    }

    @Test
    public void shouldBeAbleToLock() throws Exception {
        final Optional<Lock> lock = repository.tryLock("2222", 1, SECONDS);
        assertThat(lock).isNotNull();
        assertTrue(lock.isPresent());
    }

    @Test
    public void shouldOnlyOneLockForStateMachineBeAllowed() throws Exception {
        final Optional<Lock> lockOne = repository.tryLock("4444", 1, SECONDS);

        assertTrue(lockOne.isPresent());
        assertFalse(repository.tryLock("4444", 1, SECONDS).isPresent());
        assertFalse(repository.tryLock("4444", 1, SECONDS).isPresent());
        assertFalse(repository.tryLock("4444", 1, SECONDS).isPresent());

        lockOne.get().unlock();
        assertTrue(repository.tryLock("4444", 1, SECONDS).isPresent());
        assertFalse(repository.tryLock("4444", 1, SECONDS).isPresent());
        assertFalse(repository.tryLock("4444", 1, SECONDS).isPresent());
    }

    private Transition transitionToFinalState(String smIdTwo) {
        final Transition transition = repository.get(smIdTwo);
        return transition.setNextTransition(
                Transition.To(new TestFinalState(), new TestEvent(), transition.getContext()));
    }
}