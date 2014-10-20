package pure.fsm.core.timeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.accessor.StateMachineAccessor;
import pure.fsm.core.event.TimeoutTickEvent;
import pure.fsm.core.template.StateMachineCallback;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.core.StateMachine;

import java.util.concurrent.TimeUnit;

public class TimeoutTicker {

    private final static Logger LOG = LoggerFactory.getLogger(TimeoutTicker.class);

    private final StateMachineAccessor accessor;
    private final StateMachineTemplate template;
    private final long howOften;
    private final TimeUnit timeUnit;

    public TimeoutTicker(StateMachineAccessor accessor,
                         StateMachineTemplate template,
                         long howOften, TimeUnit timeUnit) {
        this.accessor = accessor;
        this.template = template;
        this.howOften = howOften;
        this.timeUnit = timeUnit;
    }

    public void startTickScheduler() {
        //TODO
    }

    public void endTickScheduler() {
        //TODO
    }

    public void sendTimeOutTickerEvents() {
        accessor.getAllIds().forEach(id -> template.tryWithLock(id, new StateMachineCallback() {
            @Override
            public StateMachine doWith(StateMachine stateMachine) {

                return stateMachine.handleEvent(new TimeoutTickEvent());
            }

            @Override
            public StateMachine onError(StateMachine stateMachine, Exception e) {
                LOG.debug("onError received, ignoring");
                return stateMachine;
            }

            @Override
            public void onLockFailed(Exception e) {
                LOG.debug("onLockFailed received, ignoring");
            }
        }));
    }
}