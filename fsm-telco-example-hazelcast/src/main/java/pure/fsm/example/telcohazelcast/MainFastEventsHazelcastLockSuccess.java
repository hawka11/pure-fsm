package pure.fsm.example.telcohazelcast;


import pure.fsm.java.test.fixture.event.RechargeAcceptedEvent;
import pure.fsm.java.test.fixture.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static com.google.common.collect.Sets.newHashSet;
import static pure.fsm.example.telcohazelcast.HazelcastUtil.startHzNodeOnThread;

public class MainFastEventsHazelcastLockSuccess {

    public static void main(String[] args) throws Exception {
        startHzNodeOnThread();
        Thread.sleep(2000);

        StateMachineOperations ops = new StateMachineOperations();

        final String stateMachineId = ops.createStateMachineInInitialState();

        ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555")));
        Thread.sleep(5);
        ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent("555"));

        Thread.sleep(100);
        ops.logCurrentState(stateMachineId);
    }
}
