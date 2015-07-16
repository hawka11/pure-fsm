package pure.fsm.example.inmemory;

import pure.fsm.example.inmemory.event.RechargeAcceptedEvent;
import pure.fsm.example.inmemory.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static com.google.common.collect.Sets.newHashSet;

public class MainSuccessful {

    public static void main(String[] args) throws Exception {
        StateMachineOperations ops = new StateMachineOperations();

        //create state machine
        final String stateMachineId = ops.createStateMachineInInitialState();

        //One thread will send RequestRechargeEvent to sm
        ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555", "666")));
        Thread.sleep(2000);

        //Sometime later, another thread will send RechargeAcceptedEvent to sm
        //Probably invoked by a callback via telco webservice
        ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent("555"));
        ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent("666"));
        Thread.sleep(2000);

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        ops.logCurrentState(stateMachineId);
    }
}
