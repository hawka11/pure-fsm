package pure.fsm.telco.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.state.State;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.user.domain.TelcoRechargeContext;
import pure.fsm.telco.user.domain.event.RequestAcceptedEvent;
import pure.fsm.telco.user.domain.event.RequestPinEvent;
import pure.fsm.telco.user.infra.TelcoGateway;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class InitialState extends BaseTelcoState {

    private static final Logger LOG = LoggerFactory.getLogger(InitialState.class);

    private final TelcoGateway telcoGateway;

    InitialState(TelcoStateFactory telcoStateFactory, DistributedResourceFactory resourceFactory, TelcoGateway telcoGateway) {
        super(telcoStateFactory, resourceFactory);
        this.telcoGateway = telcoGateway;
    }

    @Override
    public State accept(TelcoRechargeContext context, RequestPinEvent requestPinEvent) {
        List<String> pins = requestPinEvent.getPins();

        pins.stream().forEach(pin -> context.addResource(resourceFactory().tryLock("LOCKED_PINS", pin)));

        telcoGateway.requestPinRecharge(context.getStateMachineId(), pins);

        return this;
    }

    @Override
    public State accept(TelcoRechargeContext context, RequestAcceptedEvent requestAcceptedEvent) {
        List<String> acceptedPins = requestAcceptedEvent.getPins();

        List<String> waitingAcceptance = context.getRequestedPins().stream()
                .filter(pin -> !acceptedPins.contains(pin))
                .collect(toList());

        return waitingAcceptance.isEmpty() ?
                factory().getStateByClass(WaitingForConfirmationState.class) : this;
    }
}
