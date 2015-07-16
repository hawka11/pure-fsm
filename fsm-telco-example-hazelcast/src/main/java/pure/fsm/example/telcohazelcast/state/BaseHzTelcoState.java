package pure.fsm.example.telcohazelcast.state;

import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.example.inmemory.state.BaseTelcoState;

public class BaseHzTelcoState extends BaseTelcoState {

    private final DistributedResourceFactory distributedResourceFactory;

    public BaseHzTelcoState(DistributedResourceFactory distributedResourceFactory) {
        this.distributedResourceFactory = distributedResourceFactory;
    }

    public DistributedResourceFactory getDistributedResourceFactory() {
        return distributedResourceFactory;
    }
}
