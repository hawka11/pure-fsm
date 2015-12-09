package pure.fsm.example.inmemory;

import pure.fsm.example.inmemory.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.example.inmemory.state.BaseTelcoState.TIMEOUT_SECS;

public class MainWithTimeOut {

    public static void main(String[] args) throws Exception {
        StateMachineOperations ops = new StateMachineOperations();

        //create state machine
        final String stateMachineId = ops.createStateMachineInInitialState();

        //One thread will send RequestRechargeEvent to sm
        ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555")));
        Thread.sleep(SECONDS.toMillis(TIMEOUT_SECS + 1));

        //something should configure this to run periodically
        ops.timeoutEventTicker.tick();

        Thread.sleep(1000);

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        ops.logCurrentState(stateMachineId);
    }
}
