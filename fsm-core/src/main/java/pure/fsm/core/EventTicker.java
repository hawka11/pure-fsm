package pure.fsm.core;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static pure.fsm.core.WithinLock.tryWithLock;
import static pure.fsm.core.context.InitialContext.initialContext;
import static pure.fsm.core.repository.InProgress.inProgress;

public class EventTicker {

    private final static Logger LOG = LoggerFactory.getLogger(EventTicker.class);

    private final ScheduledExecutorService scheduledExecutorService;
    private final StateMachineRepository repository;
    private final long scheduleFrequency;
    private final TimeUnit timeUnit;
    private final Function<Transition, Transition> f;

    public EventTicker(StateMachineRepository repository,
                       long scheduleFrequency,
                       TimeUnit timeUnit,
                       Function<Transition, Transition> f) {
        this.repository = repository;
        this.scheduleFrequency = scheduleFrequency;
        this.timeUnit = timeUnit;
        this.f = f;

        scheduledExecutorService = newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder().namingPattern("pure-fsm-ticker").build());
    }

    public void start() {

        scheduledExecutorService.scheduleWithFixedDelay(this::tick, 0, scheduleFrequency, timeUnit);
    }

    public void end() {
        scheduledExecutorService.shutdown();
    }

    public void tick() {
        LOG.info("About to send out tick events.");

        try {
            inProgress(repository).stream().forEach(last -> {
                final String stateMachineId = initialContext(last.getContext()).stateMachineId;
                tryWithLock(stateMachineId, repository, f);
            });
        } catch (Exception e) {
            LOG.warn("Something went bad", e);
        }
    }
}
