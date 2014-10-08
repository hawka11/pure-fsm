package simple.fsm.optus;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import simple.fsm.accessor.hazelcast.HazelcastStateMachineAccessor;
import simple.fsm.core.StateMachine;
import simple.fsm.core.accessor.InMemoryStateMachineAccessor;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.core.template.BaseStateMachineCallback;
import simple.fsm.core.template.StateMachineTemplate;
import simple.fsm.optus.event.RechargeAcceptedEvent;
import simple.fsm.optus.event.RequestRechargeEvent;
import simple.fsm.optus.state.InitialState;

import java.math.BigDecimal;

public class MainHazelcastSuccessful {

    public static void main(String[] args) throws Exception {
        //Distributed HZ cluster running somewhere
        new Thread(() -> {
            Config config = new Config();
             Hazelcast.newHazelcastInstance(config);
        }).run();

        Thread.sleep(2000);

        final StateMachineAccessor accessor = new HazelcastStateMachineAccessor();
        final StateMachineTemplate template = new StateMachineTemplate(accessor);

        //create state machine
        final String stateMachineId = accessor.create(
                new InitialState(),
                new OptusRechargeContext());

        //One thread will send RequestRechargeEvent to sm
        new Thread(() -> template.tryWithLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public StateMachine doWith(StateMachine stateMachine) {
                RequestRechargeEvent event = new RequestRechargeEvent(new BigDecimal("20.00"));
                return stateMachine.handleEvent(event);
            }
        })).run();

        Thread.sleep(2000);

        //Sometime later, another thread will send RechargeAcceptedEvent to sm
        //Probably invoked by a callback via optus webservice
        new Thread(() -> template.tryWithLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public StateMachine doWith(StateMachine stateMachine) {
                RechargeAcceptedEvent event = new RechargeAcceptedEvent();
                return stateMachine.handleEvent(event);
            }
        })).run();

        Thread.sleep(2000);


        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        System.out.println("Ending.... current state is: " + accessor.get(stateMachineId).getCurrentState().getClass().getSimpleName());
    }
}
