package pure.fsm.dropwizard.inmemory.bundle;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import pure.fsm.core.StateFactoryRegistration;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.core.cleanup.OnCleanupListener;
import pure.fsm.core.state.StateFactory;
import pure.fsm.core.WithinLock;
import pure.fsm.core.timeout.EventTicker;
import pure.fsm.core.transition.TransitionOccuredListener;
import pure.fsm.repository.inmemory.InMemoryStateMachineRepository;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;

public abstract class StateMachineBundle implements Bundle {

    private InMemoryStateMachineRepository repository;
    private WithinLock template;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        repository = new InMemoryStateMachineRepository();
        template = new WithinLock(repository, createTransitionOccuredListeners());

        createStateFactories().stream().forEach(StateFactoryRegistration::registerStateFactory);
    }

    protected List<TransitionOccuredListener> createTransitionOccuredListeners() {
        return newArrayList();
    }

    protected abstract List<StateFactory> createStateFactories();

    public WithinLock getTemplate() {
        return template;
    }

    public EventTicker getTimeoutTicker(long howOften, TimeUnit timeUnit) {
        return new EventTicker(repository, getTemplate(), handleEvent, howOften, timeUnit);
    }

    public CleanUpFinalisedStateMachines getCleaner(Collection<OnCleanupListener> cleanupListeners,
                                                    long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                                    long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {

        return new CleanUpFinalisedStateMachines(repository, cleanupListeners, scheduleFrequency, scheduleTimeUnit, keepFinalised, keepFinalisedTimeUnit);
    }
}
