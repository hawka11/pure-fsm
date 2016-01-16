package pure.fsm.dropwizard.mysql.bundle;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import pure.fsm.core.TransitionRepository;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.core.cleanup.OnCleanupListener;
import pure.fsm.repository.mysql.MysqlTransitionRepository;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public abstract class StateMachineBundle implements ConfiguredBundle<PureFsmMysqlConfig> {

    private TransitionRepository repository;
    private DBI dbi;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(PureFsmMysqlConfig configuration, Environment environment) {

        DBIFactory dbiFactory = new DBIFactory();

        dbi = dbiFactory.build(environment, configuration.getPureFsmDatabase(), "purefsm-mysql");
        repository = new MysqlTransitionRepository(dbi);
    }

    public DBI getDbi() {
        return dbi;
    }

    public TransitionRepository getRepository() {
        return repository;
    }

    public CleanUpFinalisedStateMachines createCleaner(Collection<OnCleanupListener> cleanupListeners,
                                                       long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                                       long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {

        return new CleanUpFinalisedStateMachines(getRepository(), cleanupListeners, scheduleFrequency, scheduleTimeUnit, keepFinalised, keepFinalisedTimeUnit);
    }
}
