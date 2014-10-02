package simple.fsm.optus;

import simple.fsm.core.StateMachine;
import simple.fsm.core.accessor.InMemoryStateMachineAccessor;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.core.template.BaseStateMachineCallback;
import simple.fsm.core.template.StateMachineTemplate;
import simple.fsm.core.timeout.TimeoutTicker;
import simple.fsm.optus.event.RechargeAcceptedEvent;
import simple.fsm.optus.event.RequestRechargeEvent;
import simple.fsm.optus.state.InitialState;

import java.math.BigDecimal;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MainWithTimeOut {

    public static void main(String[] args) throws Exception {
        final StateMachineAccessor accessor = new InMemoryStateMachineAccessor();
        final StateMachineTemplate template = new StateMachineTemplate(accessor);
        final TimeoutTicker timeoutTicker = new TimeoutTicker(accessor, template, 1, SECONDS);

        //create state machine
        final String stateMachineId = accessor.create(
                new InitialState(),
                new OptusRechargeContext());

        //One thread will send RequestRechargeEvent to sm
        new Thread(() -> template.tryWithLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public StateMachine doWith(StateMachine stateMachine) {
                RequestRechargeEvent event = new RequestRechargeEvent(new BigDecimal("20.00"));
                return stateMachine.handleEvent(event);
            }
        })).run();

        Thread.sleep(6000);

        //something should configure this to run periodically
        timeoutTicker.sendTimeOutTickerEvents();

        Thread.sleep(1000);

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        System.out.println("Ending.... current state is: " + accessor.get(stateMachineId).getCurrentState().getClass().getSimpleName());
    }
}
