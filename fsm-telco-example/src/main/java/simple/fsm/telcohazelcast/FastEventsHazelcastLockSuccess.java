package simple.fsm.telcohazelcast;

import simple.fsm.telco.event.RechargeAcceptedEvent;
import simple.fsm.telco.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static simple.fsm.telcohazelcast.HazelcastUtil.startHzNodeOnThread;

public class FastEventsHazelcastLockSuccess {

    public static void main(String[] args) throws Exception {
        startHzNodeOnThread();
        Thread.sleep(2000);

        StateMachineOperations ops = new StateMachineOperations();

        final String stateMachineId = ops.createStateMachineInInitialState();

        ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00")));
        ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent());

        ops.logCurrentState(stateMachineId);
    }
}
