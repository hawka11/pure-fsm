package simple.fsm.telcohazelcast;

import simple.fsm.telco.event.RechargeAcceptedEvent;
import simple.fsm.telco.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static com.google.common.collect.Sets.newHashSet;
import static simple.fsm.telcohazelcast.HazelcastUtil.startHzNodeOnThread;

public class MainMultiHazelcastLockConflict {

    public static void main(String[] args) throws Exception {
        startHzNodeOnThread();
        Thread.sleep(2000);

        StateMachineOperations ops = new StateMachineOperations();

        final String stateMachineId1 = ops.createStateMachineInInitialState();
        final String stateMachineId2 = ops.createStateMachineInInitialState();
        final String stateMachineId3 = ops.createStateMachineInInitialState();

        ops.scheduleEventOnThread(stateMachineId1, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555")));
        Thread.sleep(100);

        //Processing this event will throw a IllegalStateException as key '555' is already locked...
        ops.scheduleEventOnThread(stateMachineId2, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555")));
        Thread.sleep(100);

        //will essentially unlock key '555' so stateMachineId3 should able to lock it.
        ops.scheduleEventOnThread(stateMachineId1, new RechargeAcceptedEvent("555"));
        Thread.sleep(100);

        //key '555' is now unlocked, successfully process event.
        ops.scheduleEventOnThread(stateMachineId3, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555")));
        Thread.sleep(100);

        ops.logCurrentState(stateMachineId1);
        ops.logCurrentState(stateMachineId2);
        ops.logCurrentState(stateMachineId3);
    }
}
