package pure.fsm.telcohazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.StateMachine;
import pure.fsm.core.Transition;
import pure.fsm.core.accessor.StateMachineContextAccessor;
import pure.fsm.core.event.Event;
import pure.fsm.core.template.BaseStateMachineCallback;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.hazelcast.accessor.HazelcastStateMachineContextAccessor;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telcohazelcast.state.HzInitialState;
import pure.fsm.telcohazelcast.state.HzTelcoStateFactory;

import static com.google.common.collect.Lists.newArrayList;
import static pure.fsm.core.StateFactoryRegistration.registerStateFactory;
import static pure.fsm.telcohazelcast.HazelcastUtil.createClientHz;
import static pure.fsm.telcohazelcast.HzTelcoRechargeContext.initialTelcoRecharge;

class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    private final StateMachineTemplate template;
    private final HzTelcoStateFactory stateFactory;
    private final HazelcastInstance hazelcastInstance;
    private final StateMachineContextAccessor accessor;

    public Transition getStateMachine(String stateMachineId) {
        return accessor.get(stateMachineId);
    }

    public StateMachineOperations() {
        final DistributedResourceFactory distributedResourceFactory = new DistributedResourceFactory();
        stateFactory = new HzTelcoStateFactory(distributedResourceFactory);

        hazelcastInstance = createClientHz();
        distributedResourceFactory.setInstance(hazelcastInstance);

        accessor = new HazelcastStateMachineContextAccessor(hazelcastInstance);
        template = new StateMachineTemplate(accessor);
    }

    public void scheduleEventOnThread(String stateMachineId, final Event event) {
        new Thread(() -> template.tryWithLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public Transition doWith(Transition prevTransition, StateMachine stateMachine) {
                return stateMachine.handleEvent(prevTransition, event);
            }
        })).start();
    }

    public String createStateMachineInInitialState() {
        registerStateFactory(stateFactory);
        return accessor.create(
                stateFactory.getStateByClass(HzInitialState.class), HzTelcoStateFactory.class, newArrayList(initialTelcoRecharge()));
    }

    public void logCurrentState(String stateMachineId) {
        LOG.info("Ending.... current state for [{}] is: [{}]", stateMachineId,
                getStateMachine(stateMachineId).getState().getClass().getSimpleName());
    }
}
