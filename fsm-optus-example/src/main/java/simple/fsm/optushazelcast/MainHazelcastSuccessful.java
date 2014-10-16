package simple.fsm.optushazelcast;

import simple.fsm.optus.event.RechargeAcceptedEvent;
import simple.fsm.optus.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static simple.fsm.optushazelcast.HazelcastUtil.startHzNodeOnThread;

public class MainHazelcastSuccessful {

    public static void main(String[] args) throws Exception {
        startHzNodeOnThread();
        Thread.sleep(2000);

        StateMachineOperations operations = new StateMachineOperations();

        final String stateMachineId = operations.createStateMachineInInitialState();

        //One thread will send RequestRechargeEvent to sm
        operations.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00")));
        Thread.sleep(2000);

        //Sometime later, another thread will send RechargeAcceptedEvent to sm
        //Probably invoked by a callback via optus webservice
        operations.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent());
        Thread.sleep(2000);

        operations.logCurrentState(stateMachineId);
    }
}
