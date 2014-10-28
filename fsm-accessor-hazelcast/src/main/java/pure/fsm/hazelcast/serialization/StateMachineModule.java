package pure.fsm.hazelcast.serialization;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hazelcast.core.HazelcastInstance;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;
import pure.fsm.hazelcast.resource.DistributedLockResource;
import pure.fsm.hazelcast.resource.DistributedLockResourceDeserializer;
import pure.fsm.hazelcast.resource.DistributedLockResourceSerializer;

public class StateMachineModule extends SimpleModule {

    public StateMachineModule(HazelcastInstance hazelcastInstance, StateFactory stateFactory) {
        addSerializer(DistributedLockResource.class, new DistributedLockResourceSerializer());
        addSerializer(State.class, new StateSerializer());

        addDeserializer(DistributedLockResource.class, new DistributedLockResourceDeserializer(hazelcastInstance));
        addDeserializer(State.class, new StateDeserializer(stateFactory));
    }
}
