package pure.fsm.telcohazelcast.state;

import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.state.BaseTelcoState;

public class BaseHzTelcoState extends BaseTelcoState {

    private final DistributedResourceFactory distributedResourceFactory;

    public BaseHzTelcoState(DistributedResourceFactory distributedResourceFactory) {
        this.distributedResourceFactory = distributedResourceFactory;
    }

    public DistributedResourceFactory getDistributedResourceFactory() {
        return distributedResourceFactory;
    }
}
