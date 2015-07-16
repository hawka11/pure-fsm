package pure.fsm.end2end.test;

import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pure.fsm.core.repository.StateMachineRepository;
import pure.fsm.end2end.hazelcast.HazelcastUtil;
import pure.fsm.repository.hazelcast.HazelcastStateMachineRepository;
import pure.fsm.repository.inmemory.InMemoryStateMachineRepository;
import pure.fsm.repository.mysql.FlywayRule;
import pure.fsm.repository.mysql.JdbiRule;
import pure.fsm.repository.mysql.MysqlStateMachineRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import static pure.fsm.end2end.hazelcast.HazelcastUtil.createClientHz;

@RunWith(Parameterized.class)
public abstract class BaseEnd2EndTest {

    static {
        HazelcastUtil.startHzNodeOnThread();
    }

    protected final Supplier<StateMachineRepository> repository;

    protected BaseEnd2EndTest(Supplier<StateMachineRepository> repository) {
        this.repository = repository;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {(Supplier<StateMachineRepository>) InMemoryStateMachineRepository::new}
                , {(Supplier<StateMachineRepository>) () -> new MysqlStateMachineRepository(JDBI_RULE.DBI)}
                , {(Supplier<StateMachineRepository>) () -> new HazelcastStateMachineRepository(createClientHz())}
        });
    }

    protected static JdbiRule JDBI_RULE = new JdbiRule("purefsm-test.yml");

    protected static FlywayRule FLYWAY_RULE = new FlywayRule(
            "fsm-repository-mysql/src/main/database/flyway-conf/flyway.fsm.properties",
            "fsm-repository-mysql/src/main/database/flyway-sql", () -> JDBI_RULE.DATA_SOURCE);
    @Rule
    public RuleChain chain = RuleChain.emptyRuleChain()
            .around(JDBI_RULE)
            .around(FLYWAY_RULE);
}
