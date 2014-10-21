package pure.fsm.telco.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.state.State;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.user.domain.TelcoRechargeContext;
import pure.fsm.telco.user.domain.event.RequestPinEvent;

import java.util.List;

public class InitialState extends BaseTelcoState {

    private static final Logger LOG = LoggerFactory.getLogger(InitialState.class);

    InitialState(DistributedResourceFactory resourceFactory) {
        super(resourceFactory);
    }

    @Override
    public State accept(TelcoRechargeContext context, RequestPinEvent requestPinEvent) {
        List<String> pins = requestPinEvent.getPins();

        pins.stream().forEach(pin -> context.addResource(resourceFactory().tryLock("LOCKED_PINS", pin)));

        return factory().getStateByClass(WaitingForAcceptance.class);
    }
}
