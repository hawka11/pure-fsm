package simple.fsm.telcohazelcast;

import simple.fsm.telco.event.RechargeAcceptedEvent;
import simple.fsm.telco.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static com.google.common.collect.Sets.newHashSet;
import static simple.fsm.telcohazelcast.HazelcastUtil.startHzNodeOnThread;

public class MainFastEventsHazelcastLockSuccess {

    public static void main(String[] args) throws Exception {
        startHzNodeOnThread();
        Thread.sleep(2000);

        StateMachineOperations ops = new StateMachineOperations();

        final String stateMachineId = ops.createStateMachineInInitialState();

        ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555")));
        ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent("555"));

        Thread.sleep(100);
        ops.logCurrentState(stateMachineId);
    }
}
