package simple.fsm.telcohazelcast.state;

import simple.fsm.core.state.StateFactory;
import simple.fsm.hazelcast.resource.DistributedResourceFactory;
import simple.fsm.telco.state.BaseTelcoState;

public class BaseHzTelcoState extends BaseTelcoState {

    private final DistributedResourceFactory distributedResourceFactory;

    public BaseHzTelcoState(DistributedResourceFactory distributedResourceFactory) {
        this.distributedResourceFactory = distributedResourceFactory;
    }

    @Override
    public StateFactory factory() {
        return new HzTelcoStateFactory(distributedResourceFactory);
    }

    public DistributedResourceFactory getDistributedResourceFactory() {
        return distributedResourceFactory;
    }
}
