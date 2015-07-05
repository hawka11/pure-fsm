package pure.fsm.jdbi.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import pure.fsm.core.StateFactoryRegistration;
import pure.fsm.core.Transition;
import pure.fsm.core.fixture.TestInitialContext;
import pure.fsm.core.fixture.TestNonFinalState;
import pure.fsm.core.fixture.TestStateFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static pure.fsm.core.Transition.initialTransition;

public class StateMachineDaoTest {

    protected static JdbiRule JDBI_RULE = new JdbiRule("purefsm-test.yml");

    protected static FlywayRule FLYWAY_RULE = new FlywayRule(
            "fsm-repository-jdbi/src/main/database/flyway-conf/flyway.fsm.properties",
            "fsm-repository-jdbi/src/main/database/flyway-sql", () -> JDBI_RULE.DATA_SOURCE);
    @Rule
    public RuleChain chain = RuleChain.emptyRuleChain()
            .around(JDBI_RULE)
            .around(FLYWAY_RULE);

    @Before
    public void beforeEach() {
        StateFactoryRegistration.registerStateFactory(new TestStateFactory());
    }

    @Test
    public void shouldGetSequentialStateMachineNumbers() {
        final StateMachineDao repository = JDBI_RULE.DBI.onDemand(StateMachineDao.class);

        assertThat(repository.getNextId()).isEqualTo("1");
        assertThat(repository.getNextId()).isEqualTo("2");
        assertThat(repository.getNextId()).isEqualTo("3");
    }

    @Test
    public void shouldMultipleThreadsGetUniqueIdsConcurrently() {

        final StateMachineDao repository = JDBI_RULE.DBI.onDemand(StateMachineDao.class);

        final List<String> firstListIds = newArrayList();
        final List<String> secondListIds = newArrayList();
        final List<String> thirdListIds = newArrayList();

        final Thread first = getNewThread(repository, firstListIds);
        final Thread second = getNewThread(repository, secondListIds);
        final Thread third = getNewThread(repository, thirdListIds);

        first.start();
        second.start();
        third.start();

        try {
            first.join();
            second.join();
            third.join();
        } catch (InterruptedException e) {
            fail("error");
        }

        assertNotInOtherLists(firstListIds, secondListIds, thirdListIds);
        assertNotInOtherLists(secondListIds, firstListIds, thirdListIds);
        assertNotInOtherLists(thirdListIds, secondListIds, firstListIds);
    }

    @Test
    public void test() {
        final StateMachineDao repository = JDBI_RULE.DBI.onDemand(StateMachineDao.class);

        final Transition transition = getInitialTestTransition();

        repository.insertStateMachineData("999", transition);

        final Transition retrieved = repository.getStateMachineData("999");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getEvent()).isEqualTo(transition.getEvent());
        assertThat(retrieved.getState()).isEqualToComparingFieldByField(transition.getState());
        assertThat(retrieved.getTransitioned()).isEqualTo(transition.getTransitioned());
        assertThat(retrieved.getContext().stateMachineId()).isEqualTo("999");
        assertThat(retrieved.getContext().stateFactory().getClass()).isEqualTo(TestStateFactory.class);
    }

    private Transition getInitialTestTransition() {
        return initialTransition("999",
                new TestNonFinalState(),
                TestStateFactory.class,
                newArrayList(new TestInitialContext("testdata")));
    }

    private void assertNotInOtherLists(List<String> primary, List<String> checkOne, List<String> checkTwo) {
        assertFalse(primary.stream().filter(checkOne::contains).findAny().isPresent());
        assertFalse(primary.stream().filter(checkTwo::contains).findAny().isPresent());
    }

    private Thread getNewThread(StateMachineDao repository, List<String> listIds) {
        return new Thread(() -> {
            System.out.println("Started thread");
            for (int i = 0; i < 100; i++) {
                listIds.add(repository.getNextId());
            }
        });
    }
}