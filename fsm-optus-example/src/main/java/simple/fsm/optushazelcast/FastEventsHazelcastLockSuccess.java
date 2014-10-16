package simple.fsm.optushazelcast;

import simple.fsm.optus.event.RechargeAcceptedEvent;
import simple.fsm.optus.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static simple.fsm.optushazelcast.HazelcastUtil.startHzNodeOnThread;

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
