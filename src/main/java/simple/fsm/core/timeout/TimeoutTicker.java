package simple.fsm.core.timeout;

import simple.fsm.core.StateMachine;
import simple.fsm.core.StateMachineCallback;
import simple.fsm.core.StateMachineTemplate;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.core.event.TimeoutTickEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeoutTicker {

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

    public void startTickSchedule() {
        //TODO
    }

    public void endTickSchedule() {
        //TODO
    }

    public void sendTimeOutTickerEvents() {
        List<StateMachine> stateMachines = accessor.getAllUnlocked();

        stateMachines.forEach(sm -> {
            template.tryInLock("", new StateMachineCallback() {
                @Override
                public void doWith(StateMachine stateMachine) {
                    stateMachine.handleEvent(new TimeoutTickEvent());
                }

                @Override
                public void onError(Exception e) {
                    //Ignore, will attempt in next tick
                }
            });
        });
    }
}
