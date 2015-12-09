package pure.fsm.dropwizard.inmemory.bundle;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import pure.fsm.core.EventTicker;
import pure.fsm.core.Transition;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.core.cleanup.OnCleanupListener;
import pure.fsm.repository.inmemory.InMemoryStateMachineRepository;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class StateMachineBundle implements Bundle {

    private InMemoryStateMachineRepository repository;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        repository = new InMemoryStateMachineRepository();
    }

    public EventTicker getTimeoutTicker(long howOften, TimeUnit timeUnit, Function<Transition, Transition> f) {
        return new EventTicker(repository, howOften, timeUnit, f);
    }

    public CleanUpFinalisedStateMachines getCleaner(Collection<OnCleanupListener> cleanupListeners,
                                                    long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                                    long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {

        return new CleanUpFinalisedStateMachines(repository, cleanupListeners, scheduleFrequency, scheduleTimeUnit, keepFinalised, keepFinalisedTimeUnit);
    }
}
