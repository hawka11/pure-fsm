package pure.fsm.jdbi.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.skife.jdbi.v2.Handle;
import pure.fsm.core.StateFactoryRegistration;
import pure.fsm.core.Transition;
import pure.fsm.core.fixture.TestEvent;
import pure.fsm.core.fixture.TestInitialContext;
import pure.fsm.core.fixture.TestNonFinalState;
import pure.fsm.core.fixture.TestStateFactory;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    public void shouldMultipleThreadsGetUniqueIdsConcurrently() throws InterruptedException {

        final StateMachineDao repository = JDBI_RULE.DBI.onDemand(StateMachineDao.class);

        final List<String> firstListIds = newArrayList();
        final List<String> secondListIds = newArrayList();
        final List<String> thirdListIds = newArrayList();

        final Thread first = newStateMachineIdGatheringThread(repository, firstListIds);
        final Thread second = newStateMachineIdGatheringThread(repository, secondListIds);
        final Thread third = newStateMachineIdGatheringThread(repository, thirdListIds);

        first.start();
        second.start();
        third.start();

        first.join();
        second.join();
        third.join();

        assertNotInOtherLists(firstListIds, secondListIds, thirdListIds);
        assertNotInOtherLists(secondListIds, firstListIds, thirdListIds);
        assertNotInOtherLists(thirdListIds, secondListIds, firstListIds);
    }

    @Test
    public void shouldSerializeAndDeserializeInsertedStateMachineData() {
        final StateMachineDao repository = JDBI_RULE.DBI.onDemand(StateMachineDao.class);

        final Transition transition = getInitialTestTransition("999");

        repository.insertStateMachineData("999", transition);

        final Transition retrieved = repository.getStateMachineData("999");
        assertInitialTransitionDetails(transition, retrieved, "999");
    }

    private void assertInitialTransitionDetails(Transition expected, Transition actual, String stateMachineId) {
        assertThat(actual).isNotNull();
        assertThat(actual.getEvent()).isEqualTo(expected.getEvent());
        assertThat(actual.getState()).isEqualToComparingFieldByField(expected.getState());
        assertThat(actual.getTransitioned()).isEqualTo(expected.getTransitioned());
        assertThat(actual.getContext().stateMachineId()).isEqualTo(stateMachineId);
        assertThat(actual.getContext().stateFactory().getClass()).isEqualTo(TestStateFactory.class);
    }

    @Test
    public void shouldSerializeAndDeserializeUpdatedStateMachineData() {
        final StateMachineDao repository = JDBI_RULE.DBI.onDemand(StateMachineDao.class);

        final Transition transition = getInitialTestTransition("3333");
        repository.insertStateMachineData("3333", transition);
        assertFalse(transition.previous().isPresent());

        final Transition nextTransition = transitionFrom(transition);
        repository.updateStateMachineData("3333", nextTransition);

        final Transition retrieved = repository.getStateMachineData("3333");
        assertTrue(retrieved.previous().isPresent());
        assertInitialTransitionDetails(nextTransition, retrieved, "3333");
    }

    @Test
    public void shouldSerializeAndDeleteStateMachineData() {
        final StateMachineDao repository = JDBI_RULE.DBI.onDemand(StateMachineDao.class);

        final Transition transition = getInitialTestTransition("7777");
        repository.insertStateMachineData("7777", transition);

        repository.removeStateMachineData("7777");

        final Transition retrieved = repository.getStateMachineData("7777");
        assertThat(retrieved).isNull();
    }

    private Transition transitionFrom(Transition transition) {
        final Transition nextTransition = Transition.To(new TestNonFinalState(),
                new TestEvent(), transition.getContext().appendState("nextState"));
        return transition.setNextTransition(nextTransition);
    }

    @Test
    public void shouldBeAbleToReentrantLock() {
        JDBI_RULE.DBI.withHandle(handle -> {
            final StateMachineDao repository = handle.attach(StateMachineDao.class);

            assertTrue(repository.lock("999", 10));
            assertTrue(repository.lock("999", 10));
            assertTrue(repository.lock("999", 10));
            return null;
        });
    }

    @Test
    public void shouldOnlyAllowOneHandleToObtainLockAtOneTime() throws InterruptedException {
        final Handle handleOne = JDBI_RULE.DBI.open();
        final Handle handleTwo = JDBI_RULE.DBI.open();
        final Handle handleThree = JDBI_RULE.DBI.open();

        assertTrue(attemptLock(handleOne, "4445"));
        assertFalse(attemptLock(handleTwo, "4445"));
        assertFalse(attemptLock(handleTwo, "4445"));
        assertFalse(attemptLock(handleThree, "4445"));
        assertFalse(attemptLock(handleThree, "4445"));

        unlock(handleOne, "4445");

        assertTrue(attemptLock(handleTwo, "4445"));
        assertFalse(attemptLock(handleThree, "4445"));
        assertFalse(attemptLock(handleThree, "4445"));

        unlock(handleTwo, "4445");

        assertTrue(attemptLock(handleThree, "4445"));
        assertFalse(attemptLock(handleOne, "4445"));
        assertFalse(attemptLock(handleOne, "4445"));
    }


    @Test
    public void shouldNotUnlockIfAttemptToUnlockWithHandleThatDidntObtainLock() throws InterruptedException {
        final Handle handleOne = JDBI_RULE.DBI.open();
        final Handle handleTwo = JDBI_RULE.DBI.open();

        assertTrue(attemptLock(handleOne, "4446"));
        assertFalse(attemptLock(handleTwo, "4446"));
        assertFalse(attemptLock(handleTwo, "4446"));

        unlock(handleTwo, "4446");

        assertTrue(attemptLock(handleOne, "4446"));
        assertFalse(attemptLock(handleTwo, "4446"));
        assertFalse(attemptLock(handleTwo, "4446"));
    }

    @Test
    public void shouldRetreiveAllIds() throws InterruptedException {
        final Handle handle = JDBI_RULE.DBI.open();
        final StateMachineDao dao = handle.attach(StateMachineDao.class);

        dao.insertStateMachineData("1", getInitialTestTransition("1"));
        dao.insertStateMachineData("2", getInitialTestTransition("2"));
        dao.insertStateMachineData("3", getInitialTestTransition("3"));

        final Set<String> ids = dao.getAllIds();

        assertThat(ids).contains("1", "2", "3");
    }

    private boolean attemptLock(Handle handle, String smId) {
        return handle.attach(StateMachineDao.class).lock(smId, 1);
    }

    private boolean unlock(Handle handle, String smId) {
        return handle.attach(StateMachineDao.class).unlock(smId);
    }

    private Transition getInitialTestTransition(String stateMachineId) {
        return initialTransition(stateMachineId,
                new TestNonFinalState(),
                TestStateFactory.class,
                newArrayList(new TestInitialContext("testdata")));
    }

    private void assertNotInOtherLists(List<String> primary, List<String> checkOne, List<String> checkTwo) {
        assertFalse(primary.stream().filter(checkOne::contains).findAny().isPresent());
        assertFalse(primary.stream().filter(checkTwo::contains).findAny().isPresent());
    }

    private Thread newStateMachineIdGatheringThread(StateMachineDao repository, List<String> listIds) {
        return new Thread(() -> {
            System.out.println("Started thread");
            for (int i = 0; i < 100; i++) {
                listIds.add(repository.getNextId());
            }
        });
    }
}