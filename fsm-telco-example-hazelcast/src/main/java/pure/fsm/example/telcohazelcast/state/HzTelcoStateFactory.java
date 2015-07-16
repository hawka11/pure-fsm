package pure.fsm.example.telcohazelcast.state;

import pure.fsm.core.state.BaseStateFactory;
import pure.fsm.core.state.State;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.example.inmemory.state.RechargeRequestedState;
import pure.fsm.example.telcohazelcast.guard.AllPinsRechargedAcceptedGuard;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class HzTelcoStateFactory extends BaseStateFactory {

    private final Map<Class<? extends State>, State> stateByStateClass = newHashMap();

    public HzTelcoStateFactory(DistributedResourceFactory distributedResourceFactory) {
        super();

        AllPinsRechargedAcceptedGuard guard = new AllPinsRechargedAcceptedGuard();

        stateByStateClass.put(HzInitialState.class, new HzInitialState(distributedResourceFactory));
        stateByStateClass.put(RechargeRequestedState.class, new RechargeRequestedState(guard));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends State> T internalGetStateByClass(Class<T> stateClass) {
        return (T) stateByStateClass.get(stateClass);
    }
}
