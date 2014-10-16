package simple.fsm.optushazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.StateMachine;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.core.event.Event;
import simple.fsm.core.template.BaseStateMachineCallback;
import simple.fsm.core.template.StateMachineTemplate;
import simple.fsm.hazelcast.accessor.HazelcastStateMachineAccessor;
import simple.fsm.hazelcast.resource.DistributedResourceFactory;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optushazelcast.state.HzInitialState;
import simple.fsm.optushazelcast.state.HzOptusStateFactory;

import static simple.fsm.optushazelcast.HazelcastUtil.createClientHz;

class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    private final StateMachineTemplate template;
    private final HzOptusStateFactory stateFactory;
    private final HazelcastInstance hazelcastInstance;
    private final StateMachineAccessor accessor;

    public StateMachine getStateMachine(String stateMachineId) {
        return accessor.get(stateMachineId);
    }

    public StateMachineOperations() {
        final DistributedResourceFactory distributedResourceFactory = new DistributedResourceFactory();
        stateFactory = new HzOptusStateFactory(distributedResourceFactory);

        hazelcastInstance = createClientHz(stateFactory);
        distributedResourceFactory.setInstance(hazelcastInstance);

        accessor = new HazelcastStateMachineAccessor(hazelcastInstance);
        template = new StateMachineTemplate(accessor);
    }

    public void scheduleEventOnThread(String stateMachineId, final Event event) {
        new Thread(() -> template.tryWithLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public StateMachine doWith(StateMachine stateMachine) {
                return stateMachine.handleEvent(event);
            }
        })).run();
    }

    public String createStateMachineInInitialState() {
        OptusRechargeContext context = new OptusRechargeContext();
        context.setMessage("testmsg");
        return accessor.create(
                stateFactory.getStateByClass(HzInitialState.class), context);
    }

    public void logCurrentState(String stateMachineId) {
        LOG.info("Ending.... current state for [{}] is: [{}]", stateMachineId,
                getStateMachine(stateMachineId).getCurrentState().getClass().getSimpleName());
    }
}
