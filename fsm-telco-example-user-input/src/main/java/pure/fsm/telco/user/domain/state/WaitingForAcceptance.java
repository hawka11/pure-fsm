package pure.fsm.telco.user.domain.state;

import pure.fsm.hazelcast.resource.DistributedResourceFactory;

public class WaitingForAcceptance extends BaseTelcoState {

    WaitingForAcceptance(DistributedResourceFactory resourceFactory) {
        super(resourceFactory);
    }
}
