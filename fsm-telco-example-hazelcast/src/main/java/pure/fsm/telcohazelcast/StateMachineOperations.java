package pure.fsm.telcohazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.repository.StateMachineRepository;
import pure.fsm.core.event.Event;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.repository.hazelcast.HazelcastStateMachineRepository;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telcohazelcast.state.HzInitialState;
import pure.fsm.telcohazelcast.state.HzTelcoStateFactory;

import static com.google.common.collect.Lists.newArrayList;
import static pure.fsm.core.StateFactoryRegistration.registerStateFactory;
import static pure.fsm.core.template.DefaultStateMachineCallable.handleWithTransition;
import static pure.fsm.telcohazelcast.HazelcastUtil.createClientHz;
import static pure.fsm.telcohazelcast.HzTelcoRechargeContext.initialTelcoRecharge;

class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    private final StateMachineTemplate template;
    private final HzTelcoStateFactory stateFactory;
    private final HazelcastInstance hazelcastInstance;
    private final StateMachineRepository repository;

    public Transition getStateMachine(String stateMachineId) {
        return repository.get(stateMachineId);
    }

    public StateMachineOperations() {
        hazelcastInstance = createClientHz();
        final DistributedResourceFactory distributedResourceFactory = new DistributedResourceFactory(hazelcastInstance);
        stateFactory = new HzTelcoStateFactory(distributedResourceFactory);

        repository = new HazelcastStateMachineRepository(hazelcastInstance);
        template = new StateMachineTemplate(repository, newArrayList());
    }

    public void scheduleEventOnThread(String stateMachineId, final Event event) {
        new Thread(() -> template.tryWithLock(stateMachineId,
                handleWithTransition((prevTransition, stateMachine) ->
                        stateMachine.handleEvent(prevTransition, event)))).start();
    }

    public String createStateMachineInInitialState() {
        registerStateFactory(stateFactory);
        return repository.create(
                stateFactory.getStateByClass(HzInitialState.class), HzTelcoStateFactory.class, newArrayList(initialTelcoRecharge()));
    }

    public void logCurrentState(String stateMachineId) {
        LOG.info("Ending.... current state for [{}] is: [{}]", stateMachineId,
                getStateMachine(stateMachineId).getState().getClass().getSimpleName());
    }
}
