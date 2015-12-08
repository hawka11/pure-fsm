package pure.fsm.core;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static pure.fsm.core.WithinLock.tryWithLock;
import static pure.fsm.core.context.InitialContext.initialContext;

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
        this.f = f;
        this.scheduleFrequency = scheduleFrequency;
        this.timeUnit = timeUnit;

        scheduledExecutorService = newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder().namingPattern("pure-fsm-ticker").build());
    }

    public void startTickScheduler() {

        scheduledExecutorService.scheduleWithFixedDelay(this::sendTimeOutTickerEvents, 0, scheduleFrequency, timeUnit);
    }

    public void endTickScheduler() {
        scheduledExecutorService.shutdown();
    }

    public void sendTimeOutTickerEvents() {
        LOG.info("About to send out time out ticker events.");

        try {
            final Set<String> inProgressIds = repository.getIds();
            inProgressIds.stream()
                    .map(repository::get)
                    .filter(last -> FinalState.class.isAssignableFrom(last.getState().getClass()))
                    .forEach(last -> {
                        final String stateMachineId = initialContext(last.getContext()).stateMachineId;
                        tryWithLock(stateMachineId, repository, f);
                    });
        } catch (Exception e) {
            LOG.warn("Something went bad", e);
        }
    }
}
