package simple.fsm.optushazelcast.state;

import simple.fsm.core.state.StateFactory;
import simple.fsm.hazelcast.resource.DistributedResourceFactory;
import simple.fsm.optus.state.BaseOptusState;

public class BaseHzOptusState extends BaseOptusState {

    private final DistributedResourceFactory distributedResourceFactory;

    public BaseHzOptusState(DistributedResourceFactory distributedResourceFactory) {
        this.distributedResourceFactory = distributedResourceFactory;
    }

    @Override
    public StateFactory factory() {
        return new HzOptusStateFactory(distributedResourceFactory);
    }

    public DistributedResourceFactory getDistributedResourceFactory() {
        return distributedResourceFactory;
    }
}
