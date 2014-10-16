package simple.fsm.optushazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import simple.fsm.core.StateMachine;
import simple.fsm.hazelcast.serialization.DistributedLockModule;
import simple.fsm.hazelcast.serialization.StateMachineSerializer;
import simple.fsm.optushazelcast.state.HzOptusStateFactory;

public class HazelcastUtil {

    static void startHzNodeOnThread() {
        new Thread(() -> {
            //Distributed HZ server cluster running somewhere
            Config config = new Config();
            Hazelcast.newHazelcastInstance(config);
        }).run();
    }

    static HazelcastInstance createClientHz(HzOptusStateFactory stateFactory) {
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
