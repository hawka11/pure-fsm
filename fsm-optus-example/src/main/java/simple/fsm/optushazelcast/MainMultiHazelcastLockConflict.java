package simple.fsm.optushazelcast;

import simple.fsm.optus.event.RechargeAcceptedEvent;
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
        final String stateMachineId3 = ops.createStateMachineInInitialState();

        ops.scheduleEventOnThread(stateMachineId1, new RequestRechargeEvent(new BigDecimal("20.00")));
        Thread.sleep(100);
        ops.scheduleEventOnThread(stateMachineId2, new RequestRechargeEvent(new BigDecimal("20.00")));
        Thread.sleep(100);

        ops.scheduleEventOnThread(stateMachineId1, new RechargeAcceptedEvent());
        Thread.sleep(100);
        ops.scheduleEventOnThread(stateMachineId3, new RequestRechargeEvent(new BigDecimal("20.00")));
        Thread.sleep(100);

        ops.logCurrentState(stateMachineId1);
        ops.logCurrentState(stateMachineId2);
        ops.logCurrentState(stateMachineId3);
    }
}
