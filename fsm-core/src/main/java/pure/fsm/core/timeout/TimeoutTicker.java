package pure.fsm.core.timeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.StateMachine;
import pure.fsm.core.Transition;
import pure.fsm.core.repository.StateMachineRepository;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.template.StateMachineCallable;
import pure.fsm.core.template.StateMachineTemplate;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Optional.of;

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

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startTickScheduler() {

        scheduledExecutorService.scheduleWithFixedDelay(this::sendTimeOutTickerEvents, 0, scheduleFrequency, timeUnit);
    }

    public void endTickScheduler() {
        scheduledExecutorService.shutdown();
    }

    public void sendTimeOutTickerEvents() {
        LOG.info("About to send out time out ticker events.");

        repository.getInProgressIds().stream()
                .filter(this::stateMachineIsTimedout)
                .forEach(id -> template.tryWithLock(id, new StateMachineCallable() {
                    @Override
                    public Optional<Transition> doWith(Transition transition, StateMachine stateMachine) {

                        return of(stateMachine.handleEvent(transition, new TimeoutTickEvent()));
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

    private boolean stateMachineIsTimedout(String id) {
        final Transition transition = template.get(id);
        return transition.getState().isTimeout(transition);
    }
}
