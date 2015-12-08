package pure.fsm.core.timeout;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.StateMachine.HandleEvent;
import pure.fsm.core.StateMachineRepository;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static pure.fsm.core.WithinLock.tryWithLock;

public class EventTicker {

    private final static Logger LOG = LoggerFactory.getLogger(EventTicker.class);

    private final StateMachineRepository repository;
    private final HandleEvent handleEvent;
    private final long scheduleFrequency;
    private final TimeUnit timeUnit;
    private final ScheduledExecutorService scheduledExecutorService;

    public EventTicker(StateMachineRepository repository,
                       HandleEvent handleEvent,
                       long scheduleFrequency, TimeUnit timeUnit) {
        this.repository = repository;
        this.handleEvent = handleEvent;
        this.scheduleFrequency = scheduleFrequency;
        this.timeUnit = timeUnit;

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder().namingPattern("pure-fsm-ticker").build());
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
            final Set<String> inProgressIds = repository.getInProgressIds();
            inProgressIds.stream().forEach(id -> tryWithLock(id, repository, handleEvent));
        } catch (Exception e) {
            LOG.warn("Something went bad", e);
        }
    }
}
