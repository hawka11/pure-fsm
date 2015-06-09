package pure.fsm.dropwizard.hazelcast.bundle;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import pure.fsm.core.StateFactoryRegistration;
import pure.fsm.core.Transition;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.core.cleanup.OnCleanupListener;
import pure.fsm.core.state.StateFactory;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.core.timeout.TimeoutTicker;
import pure.fsm.core.transition.TransitionOccuredListener;
import pure.fsm.hazelcast.repository.HazelcastStateMachineRepository;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.hazelcast.serialization.TransitionSerializer;
import pure.fsm.hazelcast.serialization.StateMachineModule;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;

public abstract class StateMachineBundle implements Bundle {

    private DistributedResourceFactory distributedResourceFactory;
    private HazelcastInstance hazelcastInstance;
    private HazelcastStateMachineRepository repository;
    private StateMachineTemplate template;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        TransitionSerializer transitionSerializer = new TransitionSerializer();

        hazelcastInstance = createClientHz(transitionSerializer);
        distributedResourceFactory = new DistributedResourceFactory(hazelcastInstance);
        repository = new HazelcastStateMachineRepository(hazelcastInstance);
        template = new StateMachineTemplate(repository, createTransitionOccuredListeners());

        createStateFactories().stream().forEach(StateFactoryRegistration::registerStateFactory);

        transitionSerializer.registerModule(new StateMachineModule(hazelcastInstance));
    }

    protected List<TransitionOccuredListener> createTransitionOccuredListeners() {
        return newArrayList();
    }

    protected abstract List<StateFactory> createStateFactories();

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

    public HazelcastStateMachineRepository getStateMachineRepository() {
        return repository;
    }

    public StateMachineTemplate getTemplate() {
        return template;
    }

    public TimeoutTicker getTimeoutTicker(long howOften, TimeUnit timeUnit) {
        return new TimeoutTicker(getStateMachineRepository(), getTemplate(), howOften, timeUnit);
    }

    public CleanUpFinalisedStateMachines getCleaner(Collection<OnCleanupListener> cleanupListeners,
                                                    long scheduleFrequency, TimeUnit scheduleTimeUnit,
                                                    long keepFinalised, ChronoUnit keepFinalisedTimeUnit) {

        return new CleanUpFinalisedStateMachines(getStateMachineRepository(), cleanupListeners, scheduleFrequency, scheduleTimeUnit, keepFinalised, keepFinalisedTimeUnit);
    }
}
