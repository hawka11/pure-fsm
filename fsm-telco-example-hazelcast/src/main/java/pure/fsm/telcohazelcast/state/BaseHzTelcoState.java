package pure.fsm.telcohazelcast.state;

import pure.fsm.core.state.StateFactory;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.state.BaseTelcoState;

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
