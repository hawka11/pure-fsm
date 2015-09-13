package pure.fsm.core.timeout;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.StateMachine;
import pure.fsm.core.Transition;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.repository.StateMachineRepository;
import pure.fsm.core.template.StateMachineCallable;
import pure.fsm.core.template.StateMachineTemplate;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeoutTicker {

    private final static Logger LOG = LoggerFactory.getLogger(TimeoutTicker.class);

    private final StateMachineRepository repository;
    private final StateMachineTemplate template;
    private final long scheduleFrequency;
    private final TimeUnit timeUnit;
    private final ScheduledExecutorService scheduledExecutorService;

    public TimeoutTicker(StateMachineRepository repository,
                         StateMachineTemplate template,
                         long scheduleFrequency, TimeUnit timeUnit) {
        this.repository = repository;
        this.template = template;
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
            doSendEvents();
        } catch (Exception e) {
            LOG.warn("Something went bad", e);
        }
    }

    private void doSendEvents() {
        final Set<String> inProgressIds = repository.getInProgressIds();
        inProgressIds.stream()
                .filter(this::stateMachineIsTimedOut)
                .forEach(id -> template.tryWithLock(id, new StateMachineCallable() {
                    @Override
                    public Transition doWith(Transition transition, StateMachine stateMachine) {

                        LOG.info("Determined State Machine [{}] is timed out, sending timeout tick event", id);

                        return stateMachine.handleEvent(transition, new TimeoutTickEvent());
                    }

                    @Override
                    public Transition onError(Transition prevTransition, StateMachine stateMachine, Exception e) {
                        LOG.debug("onError received, ignoring");
                        return prevTransition;
                    }

                    @Override
                    public void onLockFailed(Exception e) {
                        LOG.debug("onLockFailed received, ignoring");
                    }
                }));
    }

    private boolean stateMachineIsTimedOut(String id) {
        final Transition transition = template.get(id);
        final boolean timeout = transition.getState().isTimeout(transition);
        if (LOG.isDebugEnabled()) {
            LOG.debug("state machine [{}] timeout is [{}]", id, timeout);
        }
        return timeout;
    }
}
