package pure.fsm.example.telcohazelcast;

import pure.fsm.example.inmemory.event.RechargeAcceptedEvent;
import pure.fsm.example.inmemory.event.RequestRechargeEvent;

import java.math.BigDecimal;

import static com.google.common.collect.Sets.newHashSet;
import static pure.fsm.example.telcohazelcast.HazelcastUtil.startHzNodeOnThread;

public class MainHazelcastSuccessful {

    public static void main(String[] args) throws Exception {
        startHzNodeOnThread();
        Thread.sleep(2000);

        StateMachineOperations ops = new StateMachineOperations();

        final String stateMachineId = ops.createStateMachineInInitialState();

        //One thread will send RequestRechargeEvent to sm
        ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555", "666")));
        Thread.sleep(2000);

        //Sometime later, another thread will send RechargeAcceptedEvent to sm
        //Probably invoked by a callback via telco webservice
        ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent("555"));
        ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent("666"));
        Thread.sleep(2000);

        ops.logCurrentState(stateMachineId);
    }
}
