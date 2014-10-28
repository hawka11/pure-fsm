package pure.fsm.telcohazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;
import pure.fsm.core.accessor.StateMachineContextAccessor;
import pure.fsm.core.event.Event;
import pure.fsm.core.template.BaseStateMachineCallback;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.hazelcast.accessor.HazelcastStateMachineContextAccessor;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telcohazelcast.state.HzInitialState;
import pure.fsm.telcohazelcast.state.HzTelcoStateFactory;

import static pure.fsm.telcohazelcast.HazelcastUtil.createClientHz;

class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    private final StateMachineTemplate template;
    private final HzTelcoStateFactory stateFactory;
    private final HazelcastInstance hazelcastInstance;
    private final StateMachineContextAccessor accessor;

    public Context getStateMachine(String stateMachineId) {
        return accessor.get(stateMachineId);
    }

    public StateMachineOperations() {
        final DistributedResourceFactory distributedResourceFactory = new DistributedResourceFactory();
        stateFactory = new HzTelcoStateFactory(distributedResourceFactory);

        hazelcastInstance = createClientHz(stateFactory);
        distributedResourceFactory.setInstance(hazelcastInstance);

        accessor = new HazelcastStateMachineContextAccessor(hazelcastInstance);
        template = new StateMachineTemplate(accessor);
    }

    public void scheduleEventOnThread(String stateMachineId, final Event event) {
        new Thread(() -> template.tryWithLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public Context doWith(Context context, StateMachine stateMachine) {
                return stateMachine.handleEvent(context, event);
            }
        })).start();
    }

    public String createStateMachineInInitialState() {
        HzTelcoRechargeContext context = new HzTelcoRechargeContext();
        context.setMessage("testmsg");
        return accessor.create(
                stateFactory.getStateByClass(HzInitialState.class), context);
    }

    public void logCurrentState(String stateMachineId) {
        LOG.info("Ending.... current state for [{}] is: [{}]", stateMachineId,
                getStateMachine(stateMachineId).getCurrentState().getClass().getSimpleName());
    }
}
