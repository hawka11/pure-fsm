package pure.fsm.jdbi.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class StateMachineIdRepositoryTest {

    protected static JdbiRule JDBI_RULE = new JdbiRule("purefsm-test.yml");

    protected static FlywayRule FLYWAY_RULE = new FlywayRule(
            "fsm-repository-jdbi/src/main/database/flyway-conf/flyway.fsm.properties",
            "fsm-repository-jdbi/src/main/database/flyway-sql", () -> JDBI_RULE.DATA_SOURCE);
    @Rule
    public RuleChain chain = RuleChain.emptyRuleChain()
            .around(JDBI_RULE)
            .around(FLYWAY_RULE);

    @Test
    public void shouldGetSequentialStateMachineNumbers() {
        final StateMachineIdRepository repository = JDBI_RULE.DBI.onDemand(StateMachineIdRepository.class);

        assertThat(repository.getNextId()).isEqualTo("1");
        assertThat(repository.getNextId()).isEqualTo("2");
        assertThat(repository.getNextId()).isEqualTo("3");
    }

    @Test
    public void shouldMultipleThreadsGetUniqueIdsConcurrently() {

        final StateMachineIdRepository repository = JDBI_RULE.DBI.onDemand(StateMachineIdRepository.class);

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

    private void assertNotInOtherLists(List<String> primary, List<String> checkOne, List<String> checkTwo) {
        assertFalse(primary.stream().filter(checkOne::contains).findAny().isPresent());
        assertFalse(primary.stream().filter(checkTwo::contains).findAny().isPresent());
    }

    private Thread getNewThread(StateMachineIdRepository repository, List<String> listIds) {
        return new Thread(() -> {
            System.out.println("Started thread");
            for (int i = 0; i < 100; i++) {
                listIds.add(repository.getNextId());
            }
        });
    }
}