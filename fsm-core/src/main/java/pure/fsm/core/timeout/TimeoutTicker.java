package pure.fsm.core.timeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.StateMachine;
import pure.fsm.core.Transition;
import pure.fsm.core.accessor.StateMachineContextAccessor;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.template.StateMachineCallback;
import pure.fsm.core.template.StateMachineTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeoutTicker {

    private final static Logger LOG = LoggerFactory.getLogger(TimeoutTicker.class);

    private final StateMachineContextAccessor accessor;
    private final StateMachineTemplate template;
    private final long scheduleFrequency;
    private final TimeUnit timeUnit;
    private final ScheduledExecutorService scheduledExecutorService;

    public TimeoutTicker(StateMachineContextAccessor accessor,
                         StateMachineTemplate template,
                         long scheduleFrequency, TimeUnit timeUnit) {
        this.accessor = accessor;
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

        accessor.getAllNonFinalIds().stream()
                .filter(this::stateMachineIsTimedout)
                .forEach(id -> template.tryWithLock(id, new StateMachineCallback() {
                    @Override
                    public Transition doWith(Transition transition, StateMachine stateMachine) {

                        return stateMachine.handleEvent(transition, new TimeoutTickEvent());
                    }

                    @Override
                    public Transition onError(Transition transition, StateMachine stateMachine, Exception e) {
                        LOG.debug("onError received, ignoring");
                        return transition;
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
