package pure.fsm.jdbi.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

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
    public void test() {
        final StateMachineIdRepository repository = JDBI_RULE.DBI.onDemand(StateMachineIdRepository.class);

        final String firstId = repository.getNextId();
        final String secondId = repository.getNextId();
    }
}