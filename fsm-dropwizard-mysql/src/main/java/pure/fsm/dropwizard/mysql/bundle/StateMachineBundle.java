package pure.fsm.dropwizard.mysql.bundle;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;
import pure.fsm.core.StateFactoryRegistration;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.core.cleanup.OnCleanupListener;
import pure.fsm.core.repository.StateMachineRepository;
import pure.fsm.core.state.StateFactory;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.core.timeout.TimeoutTicker;
import pure.fsm.core.transition.TransitionOccuredListener;
import pure.fsm.repository.mysql.MysqlStateMachineRepository;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;

public abstract class StateMachineBundle implements Bundle {

    private StateMachineRepository repository;
    private StateMachineTemplate template;
    private final DBI dbi;

    protected StateMachineBundle(DBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        repository = new MysqlStateMachineRepository(dbi);
        template = new StateMachineTemplate(repository, createTransitionOccuredListeners());

        createStateFactories().stream().forEach(StateFactoryRegistration::registerStateFactory);

    }

    protected List<TransitionOccuredListener> createTransitionOccuredListeners() {
        return newArrayList();
    }

    protected abstract List<StateFactory> createStateFactories();


    public StateMachineRepository getStateMachineRepository() {
        return repository;
    }

    public StateMachineTemplate getTemplate() {
        return template;
    }

    public TimeoutTicker getTimeoutTicker(long howOften, TimeUnit timeUnit) {
        return new TimeoutTicker(getStateMachineRepository(), getTemplate(), howOften, timeUnit);
    }

    public CleanUpFinalisedStateMachines getCleaner(Collection<OnCleanupListener> cleanupListeners,
                                                    long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                                    long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {

        return new CleanUpFinalisedStateMachines(getStateMachineRepository(), cleanupListeners, scheduleFrequency, scheduleTimeUnit, keepFinalised, keepFinalisedTimeUnit);
    }
}
