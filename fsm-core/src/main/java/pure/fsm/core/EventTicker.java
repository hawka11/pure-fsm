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
import static pure.fsm.core.repository.InProgress.inProgressTransitions;

public class EventTicker {

    private final static Logger LOG = LoggerFactory.getLogger(EventTicker.class);

    private final ScheduledExecutorService scheduledExecutorService;
    private final TransitionRepository repository;
    private final long scheduleFrequency;
    private final TimeUnit timeUnit;
    private final Function<Transition, Boolean> shouldSend;
    private final Function<Transition, Transition> f;

    public EventTicker(ScheduledExecutorService scheduledExecutorService,
                       TransitionRepository repository,
                       long scheduleFrequency,
                       TimeUnit timeUnit,
                       Function<Transition, Boolean> shouldSend,
                       Function<Transition, Transition> f) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.repository = repository;
        this.scheduleFrequency = scheduleFrequency;
        this.timeUnit = timeUnit;
        this.shouldSend = shouldSend;
        this.f = f;
    }

    public static EventTicker defaultTicker(TransitionRepository repository,
                                            long scheduleFrequency,
                                            TimeUnit timeUnit,
                                            Function<Transition, Boolean> shouldSend,
                                            Function<Transition, Transition> f) {

        final ScheduledExecutorService executor = newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder().namingPattern("pure-fsm-ticker").build());

        return new EventTicker(executor, repository, scheduleFrequency, timeUnit, shouldSend, f);
    }

    public static EventTicker defaultAlwaysTicker(TransitionRepository repository,
                                                  long scheduleFrequency,
                                                  TimeUnit timeUnit,
                                                  Function<Transition, Transition> f) {

        return defaultTicker(repository, scheduleFrequency, timeUnit, t -> true, f);
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
            inProgressTransitions(repository).stream()
                    .filter(shouldSend::apply)
                    .forEach(t -> {
                        final String stateMachineId = initialContext(t.getContext()).stateMachineId;
                        tryWithLock(stateMachineId, repository, f);
                    });
        } catch (Exception e) {
            LOG.warn("Something went bad on timeout tick", e);
        }
    }
}
