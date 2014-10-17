package simple.fsm.telcohazelcast;

import simple.fsm.telco.event.RechargeAcceptedEvent;
import simple.fsm.telco.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static com.google.common.collect.Sets.newHashSet;
import static simple.fsm.telcohazelcast.HazelcastUtil.startHzNodeOnThread;

public class MainHazelcastSuccessful {

    public static void main(String[] args) throws Exception {
        startHzNodeOnThread();
        Thread.sleep(2000);

        StateMachineOperations ops = new StateMachineOperations();

        final String stateMachineId = ops.createStateMachineInInitialState();

        //One thread will send RequestRechargeEvent to sm
        ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555")));
        Thread.sleep(2000);

        //Sometime later, another thread will send RechargeAcceptedEvent to sm
        //Probably invoked by a callback via telco webservice
        ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent());
        Thread.sleep(2000);

        ops.logCurrentState(stateMachineId);
    }
}
