package pure.fsm.example.telcohazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import pure.fsm.core.Transition;
import pure.fsm.repository.hazelcast.serialization.TransitionSerializer;
import pure.fsm.repository.hazelcast.serialization.StateMachineModule;

public class HazelcastUtil {

    static void startHzNodeOnThread() {
        new Thread(() -> {
            //Distributed HZ server cluster running somewhere
            Config config = new Config();
            Hazelcast.newHazelcastInstance(config);
        }).run();
    }

    static HazelcastInstance createClientHz() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addAddress("127.0.0.1:5701");

        final TransitionSerializer transitionSerializer = new TransitionSerializer();

        SerializationConfig serializationConfig = clientConfig.getSerializationConfig();
        serializationConfig.getSerializerConfigs()
                .add(new SerializerConfig().setTypeClass(Transition.class).setImplementation(transitionSerializer));

        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

        transitionSerializer.registerModule(new StateMachineModule(hazelcastInstance));

        return hazelcastInstance;
    }
}
