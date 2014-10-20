package pure.fsm.telcohazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import pure.fsm.core.StateMachine;
import pure.fsm.hazelcast.serialization.DistributedLockModule;
import pure.fsm.hazelcast.serialization.StateMachineSerializer;
import pure.fsm.telcohazelcast.state.HzTelcoStateFactory;

public class HazelcastUtil {

    static void startHzNodeOnThread() {
        new Thread(() -> {
            //Distributed HZ server cluster running somewhere
            Config config = new Config();
            Hazelcast.newHazelcastInstance(config);
        }).run();
    }

    static HazelcastInstance createClientHz(HzTelcoStateFactory stateFactory) {
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
