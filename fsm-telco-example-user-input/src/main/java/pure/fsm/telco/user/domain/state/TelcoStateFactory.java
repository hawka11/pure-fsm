package pure.fsm.telco.user.domain.state;

import pure.fsm.core.state.BaseStateFactory;
import pure.fsm.core.state.State;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class TelcoStateFactory extends BaseStateFactory {

    private final Map<Class<? extends State>, State> stateByStateClass = newHashMap();

    public TelcoStateFactory(DistributedResourceFactory distributedResourceFactory) {
        super();

        stateByStateClass.put(InitialState.class, new InitialState(distributedResourceFactory));
        stateByStateClass.put(WaitingForAcceptance.class, new WaitingForAcceptance(distributedResourceFactory));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends State> T internalGetStateByClass(Class<T> stateClass) {
        return (T) stateByStateClass.get(stateClass);
    }
}
