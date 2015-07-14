package pure.fsm.jdbi.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import pure.fsm.core.StateFactoryRegistration;
import pure.fsm.core.fixture.TestStateFactory;
import pure.fsm.core.repository.StateMachineRepository.Lock;

import java.util.Optional;

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
    public void testSimpleTryLock() throws Exception {
        final Optional<Lock> lock = repository.tryLock("2222", 1, SECONDS);
        assertThat(lock).isNotNull();
        assertTrue(lock.isPresent());
    }

    @Test
    public void testTryLock() throws Exception {
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
}