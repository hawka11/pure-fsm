package simple.fsm.optushazelcast;

import simple.fsm.optus.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static simple.fsm.optushazelcast.HazelcastUtil.startHzNodeOnThread;

public class MainMultiHazelcastLockConflict {

    public static void main(String[] args) throws Exception {
        startHzNodeOnThread();
        Thread.sleep(2000);

        StateMachineOperations ops = new StateMachineOperations();

        final String stateMachineId1 = ops.createStateMachineInInitialState();
        final String stateMachineId2 = ops.createStateMachineInInitialState();

        //One thread will send RequestRechargeEvent to sm
        ops.scheduleEventOnThread(stateMachineId1, new RequestRechargeEvent(new BigDecimal("20.00")));
        ops.scheduleEventOnThread(stateMachineId2, new RequestRechargeEvent(new BigDecimal("20.00")));
        Thread.sleep(2000);

        ops.logCurrentState(stateMachineId1);
        ops.logCurrentState(stateMachineId2);
    }
}
