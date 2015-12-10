package pure.fsm.dropwizard.hazelcast.bundle;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import pure.fsm.core.EventTicker;
import pure.fsm.core.Transition;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.core.cleanup.OnCleanupListener;
import pure.fsm.repository.hazelcast.HazelcastTransitionRepository;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.repository.hazelcast.serialization.StateMachineModule;
import pure.fsm.repository.hazelcast.serialization.TransitionSerializer;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class StateMachineBundle implements Bundle {

    private DistributedResourceFactory distributedResourceFactory;
    private HazelcastInstance hazelcastInstance;
    private HazelcastTransitionRepository repository;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        TransitionSerializer transitionSerializer = new TransitionSerializer();

        hazelcastInstance = createClientHz(transitionSerializer);
        distributedResourceFactory = new DistributedResourceFactory(hazelcastInstance);
        repository = new HazelcastTransitionRepository(hazelcastInstance);

        transitionSerializer.registerModule(new StateMachineModule(hazelcastInstance));
    }

    private HazelcastInstance createClientHz(TransitionSerializer transitionSerializer) {
        ClientConfig clientConfig = getClientConfig();

        SerializationConfig serializationConfig = clientConfig.getSerializationConfig();
        serializationConfig.getSerializerConfigs()
                .add(new SerializerConfig().setTypeClass(Transition.class).setImplementation(transitionSerializer));

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

    public HazelcastTransitionRepository getRepository() {
        return repository;
    }

    public EventTicker createEventTicker(long howOften, TimeUnit timeUnit, Function<Transition, Transition> f) {
        return new EventTicker(repository, howOften, timeUnit, f);
    }

    public CleanUpFinalisedStateMachines createCleaner(Collection<OnCleanupListener> cleanupListeners,
                                                    long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                                    long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {

        return new CleanUpFinalisedStateMachines(getRepository(), cleanupListeners, scheduleFrequency, scheduleTimeUnit, keepFinalised, keepFinalisedTimeUnit);
    }
}
