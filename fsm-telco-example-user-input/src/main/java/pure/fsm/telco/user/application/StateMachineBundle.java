package pure.fsm.telco.user.application;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import pure.fsm.core.StateMachine;
import pure.fsm.core.accessor.CleanUpFinalisedStateMachines;
import pure.fsm.core.state.StateFactory;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.core.timeout.TimeoutTicker;
import pure.fsm.hazelcast.accessor.HazelcastStateMachineAccessor;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.hazelcast.serialization.DistributedLockModule;
import pure.fsm.hazelcast.serialization.StateMachineSerializer;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public abstract class StateMachineBundle implements Bundle {

    private DistributedResourceFactory distributedResourceFactory;
    private StateFactory stateFactory;
    private HazelcastInstance hazelcastInstance;
    private HazelcastStateMachineAccessor accessor;
    private StateMachineTemplate template;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        distributedResourceFactory = new DistributedResourceFactory();
        stateFactory = createStateFactory();
        hazelcastInstance = createClientHz(stateFactory);
        distributedResourceFactory.setInstance(hazelcastInstance);
        accessor = new HazelcastStateMachineAccessor(hazelcastInstance);
        template = new StateMachineTemplate(accessor);
    }

    protected abstract StateFactory createStateFactory();

    private HazelcastInstance createClientHz(StateFactory stateFactory) {
        ClientConfig clientConfig = getClientConfig();

        final StateMachineSerializer stateMachineSerializer = new StateMachineSerializer(stateFactory);

        SerializationConfig serializationConfig = clientConfig.getSerializationConfig();
        serializationConfig.getSerializerConfigs()
                .add(new SerializerConfig().setTypeClass(StateMachine.class).setImplementation(stateMachineSerializer));

        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

        stateMachineSerializer.registerModule(new DistributedLockModule(hazelcastInstance));

        return hazelcastInstance;
    }

    protected ClientConfig getClientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addAddress("127.0.0.1:5701"); //TODO: configuredBundle
        return clientConfig;
    }

    public DistributedResourceFactory getDistributedResourceFactory() {
        return distributedResourceFactory;
    }

    public StateFactory getStateFactory() {
        return stateFactory;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public HazelcastStateMachineAccessor getAccessor() {
        return accessor;
    }

    public StateMachineTemplate getTemplate() {
        return template;
    }

    public TimeoutTicker getTimeoutTicker(long howOften, TimeUnit timeUnit) {
        return new TimeoutTicker(getAccessor(), getTemplate(), howOften, timeUnit);
    }

    public CleanUpFinalisedStateMachines getCleaner(long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                                    long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {

        return new CleanUpFinalisedStateMachines(getAccessor(), scheduleFrequency, scheduleTimeUnit, keepFinalised, keepFinalisedTimeUnit);
    }
}
