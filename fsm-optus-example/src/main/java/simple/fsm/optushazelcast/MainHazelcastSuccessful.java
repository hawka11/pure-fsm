package simple.fsm.optushazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import simple.fsm.core.StateMachine;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.core.template.BaseStateMachineCallback;
import simple.fsm.core.template.StateMachineTemplate;
import simple.fsm.hazelcast.accessor.HazelcastStateMachineAccessor;
import simple.fsm.hazelcast.resource.DistributedResourceFactory;
import simple.fsm.hazelcast.serialization.DistributedLockModule;
import simple.fsm.hazelcast.serialization.StateMachineSerializer;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optus.event.RechargeAcceptedEvent;
import simple.fsm.optus.event.RequestRechargeEvent;
import simple.fsm.optushazelcast.state.HzInitialState;
import simple.fsm.optushazelcast.state.HzOptusStateFactory;

import java.math.BigDecimal;

public class MainHazelcastSuccessful {

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            //Distributed HZ server cluster running somewhere
            Config config = new Config();
            Hazelcast.newHazelcastInstance(config);
        }).run();

        Thread.sleep(2000);

        //Start Setup Beans
        final DistributedResourceFactory distributedResourceFactory = new DistributedResourceFactory();
        final HzOptusStateFactory stateFactory = new HzOptusStateFactory(distributedResourceFactory);

        final HazelcastInstance hazelcastInstance = createClientHz(stateFactory);
        distributedResourceFactory.setInstance(hazelcastInstance);

        final StateMachineAccessor accessor = new HazelcastStateMachineAccessor(hazelcastInstance);
        final StateMachineTemplate template = new StateMachineTemplate(accessor);
        //End Setup Beans

        //create state machine
        final String stateMachineId = createStateMachineInInitialState(stateFactory, accessor);

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

    private static String createStateMachineInInitialState(HzOptusStateFactory stateFactory, StateMachineAccessor accessor) {
        OptusRechargeContext context = new OptusRechargeContext();
        context.setMessage("testmsg");
        return accessor.create(
                stateFactory.getStateByClass(HzInitialState.class), context);
    }

    private static HazelcastInstance createClientHz(HzOptusStateFactory stateFactory) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addAddress("127.0.0.1:5701");

        final StateMachineSerializer stateMachineSerializer = new StateMachineSerializer(stateFactory);

        SerializationConfig serializationConfig = clientConfig.getSerializationConfig();
        serializationConfig.getSerializerConfigs()
                .add(new SerializerConfig().setTypeClass(StateMachine.class).setImplementation(stateMachineSerializer));

        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

        stateMachineSerializer.registerModule(new DistributedLockModule(hazelcastInstance));

        return hazelcastInstance;
    }
}
