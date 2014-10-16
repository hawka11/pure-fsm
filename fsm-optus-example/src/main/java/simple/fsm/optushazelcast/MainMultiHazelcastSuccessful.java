package simple.fsm.optushazelcast;

import simple.fsm.optus.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static simple.fsm.optushazelcast.HazelcastUtil.startHzNodeOnThread;

public class MainMultiHazelcastSuccessful {

    public static void main(String[] args) throws Exception {
        startHzNodeOnThread();
        Thread.sleep(2000);

        StateMachineOperations operations = new StateMachineOperations();

        final String stateMachineId1 = operations.createStateMachineInInitialState();
        final String stateMachineId2 = operations.createStateMachineInInitialState();

        //One thread will send RequestRechargeEvent to sm
        operations.scheduleEventOnThread(stateMachineId1, new RequestRechargeEvent(new BigDecimal("20.00")));
        operations.scheduleEventOnThread(stateMachineId2, new RequestRechargeEvent(new BigDecimal("20.00")));
        Thread.sleep(2000);

        operations.logCurrentState(stateMachineId1);
        operations.logCurrentState(stateMachineId2);
    }
}
