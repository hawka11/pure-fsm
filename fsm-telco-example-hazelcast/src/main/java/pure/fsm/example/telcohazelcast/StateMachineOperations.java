package pure.fsm.example.telcohazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.StateMachineRepository;
import pure.fsm.core.Transition;
import pure.fsm.java.test.fixture.event.TelcoEvent;
import pure.fsm.example.telcohazelcast.guard.AllPinsRechargedAcceptedGuard;
import pure.fsm.repository.hazelcast.HazelcastStateMachineRepository;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;

import static com.google.common.collect.Lists.newArrayList;
import static pure.fsm.core.WithinLock.tryWithLock;
import static pure.fsm.example.telcohazelcast.HazelcastUtil.createClientHz;
import static pure.fsm.example.telcohazelcast.HzTelcoRechargeContext.initialTelcoRecharge;
import static pure.fsm.example.telcohazelcast.state.HzInitialState.INITIAL_STATE;

public class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    public final HazelcastInstance hazelcastInstance;
    public final StateMachineRepository repository;
    public final DistributedResourceFactory distributedResourceFactory;
    public final HzTelcoStateMachine stateMachine;

    public Transition getStateMachine(String stateMachineId) {
        return repository.get(stateMachineId);
    }

    public StateMachineOperations() {
        hazelcastInstance = createClientHz();
        distributedResourceFactory = new DistributedResourceFactory(hazelcastInstance);
        repository = new HazelcastStateMachineRepository(hazelcastInstance);
        stateMachine = new HzTelcoStateMachine(new AllPinsRechargedAcceptedGuard(), distributedResourceFactory);
    }

    public void scheduleEventOnThread(String stateMachineId, final TelcoEvent event) {

        new Thread(() -> tryWithLock(stateMachineId, repository, (last) -> stateMachine.handleEvent(last, event))).start();
    }

    public String createStateMachineInInitialState() {
        return repository.create(INITIAL_STATE, newArrayList(initialTelcoRecharge()));
    }

    public void logCurrentState(String stateMachineId) {
        LOG.info("Ending.... current state for [{}] is: [{}]", stateMachineId,
                getStateMachine(stateMachineId).getState().getClass().getSimpleName());
    }
}
