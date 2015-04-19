package pure.fsm.telco.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.user.domain.TelcoRechargeData;
import pure.fsm.telco.user.domain.event.RequestAcceptedEvent;
import pure.fsm.telco.user.domain.event.RequestPinEvent;
import pure.fsm.telco.user.infra.TelcoGateway;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static pure.fsm.core.context.MostRecentTrait.mostRecentOf;
import static pure.fsm.core.trait.InitialContext.initialContext;

public class InitialState extends BaseTelcoState {

    private static final Logger LOG = LoggerFactory.getLogger(InitialState.class);

    private final TelcoGateway telcoGateway;

    InitialState(DistributedResourceFactory resourceFactory, TelcoGateway telcoGateway) {
        super(resourceFactory);
        this.telcoGateway = telcoGateway;
    }

    @Override
    public Transition accept(Transition transition, RequestPinEvent requestPinEvent) {
        List<String> pins = requestPinEvent.getPins();

        pins.stream().forEach(pin -> transition.appendContext(resourceFactory().tryLock("LOCKED_PINS", pin)));

        telcoGateway.requestPinRecharge(initialContext(transition).stateMachineId, pins);

        return transition.transitionTo(this, requestPinEvent);
    }

    @Override
    public Transition accept(Transition transition, RequestAcceptedEvent requestAcceptedEvent) {
        List<String> acceptedPins = requestAcceptedEvent.getPins();

        final TelcoRechargeData data = mostRecentOf(transition, TelcoRechargeData.class).get();

        List<String> waitingAcceptance = data.requestedPins(transition).stream()
                .filter(pin -> !acceptedPins.contains(pin))
                .collect(toList());

        final BaseTelcoState nextState = waitingAcceptance.isEmpty() ?
                transition.stateFactory().getStateByClass(WaitingForConfirmationState.class) : this;

        return transition.transitionTo(nextState, requestAcceptedEvent);
    }
}
