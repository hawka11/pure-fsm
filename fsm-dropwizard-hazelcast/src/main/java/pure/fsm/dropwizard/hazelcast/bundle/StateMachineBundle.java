package pure.fsm.dropwizard.hazelcast.bundle;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import pure.fsm.core.Transition;
import pure.fsm.core.accessor.CleanUpFinalisedStateMachines;
import pure.fsm.core.accessor.OnCleanupListener;
import pure.fsm.core.state.StateFactory;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.core.timeout.TimeoutTicker;
import pure.fsm.hazelcast.accessor.HazelcastStateMachineContextAccessor;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.hazelcast.serialization.ContextSerializer;
import pure.fsm.hazelcast.serialization.StateMachineModule;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static pure.fsm.core.StateFactoryRegistration.registerStateFactory;

public abstract class StateMachineBundle implements Bundle {

    private DistributedResourceFactory distributedResourceFactory;
    private HazelcastInstance hazelcastInstance;
    private HazelcastStateMachineContextAccessor accessor;
    private StateMachineTemplate template;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        ContextSerializer contextSerializer = new ContextSerializer();

        distributedResourceFactory = new DistributedResourceFactory();
        hazelcastInstance = createClientHz(contextSerializer);
        accessor = new HazelcastStateMachineContextAccessor(hazelcastInstance);
        template = new StateMachineTemplate(accessor);

        registerStateFactory(createStateFactory());

        distributedResourceFactory.setInstance(hazelcastInstance);

        contextSerializer.registerModule(new StateMachineModule(hazelcastInstance));
    }

    protected abstract StateFactory createStateFactory();

    private HazelcastInstance createClientHz(ContextSerializer contextSerializer) {
        ClientConfig clientConfig = getClientConfig();

        SerializationConfig serializationConfig = clientConfig.getSerializationConfig();
        serializationConfig.getSerializerConfigs()
                .add(new SerializerConfig().setTypeClass(Transition.class).setImplementation(contextSerializer));

        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    protected ClientConfig getClientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addAddress("127.0.0.1:5701"); //TODO: configuredBundle
        return clientConfig;
    }

    public DistributedResourceFactory getDistributedResourceFactory() {
        return distributedResourceFactory;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public HazelcastStateMachineContextAccessor getAccessor() {
        return accessor;
    }

    public StateMachineTemplate getTemplate() {
        return template;
    }

    public TimeoutTicker getTimeoutTicker(long howOften, TimeUnit timeUnit) {
        return new TimeoutTicker(getAccessor(), getTemplate(), howOften, timeUnit);
    }

    public CleanUpFinalisedStateMachines getCleaner(Collection<OnCleanupListener> cleanupListeners,
                                                    long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                                    long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {

        return new CleanUpFinalisedStateMachines(getAccessor(), cleanupListeners, scheduleFrequency, scheduleTimeUnit, keepFinalised, keepFinalisedTimeUnit);
    }
}
