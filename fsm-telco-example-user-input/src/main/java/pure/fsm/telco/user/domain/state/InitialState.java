package pure.fsm.telco.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.user.domain.TelcoRechargeData;
import pure.fsm.telco.user.domain.event.RequestAcceptedEvent;
import pure.fsm.telco.user.domain.event.RequestPinEvent;
import pure.fsm.telco.user.infra.TelcoGateway;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static pure.fsm.core.context.InitialContext.initialContext;

public class InitialState extends BaseTelcoState {

    private static final Logger LOG = LoggerFactory.getLogger(InitialState.class);

    private final TelcoGateway telcoGateway;

    InitialState(DistributedResourceFactory resourceFactory, TelcoGateway telcoGateway) {
        super(resourceFactory);
        this.telcoGateway = telcoGateway;
    }

    @Override
    public Transition accept(Context context, RequestPinEvent requestPinEvent) {
        List<String> pins = requestPinEvent.getPins();

        pins.stream().forEach(pin -> context.addCanUnlock(resourceFactory().tryLock("LOCKED_PINS", pin)));

        telcoGateway.requestPinRecharge(initialContext(context).stateMachineId, pins);

        return Transition.To(this, requestPinEvent, context);
    }

    @Override
    public Transition accept(Context context, RequestAcceptedEvent requestAcceptedEvent) {
        List<String> acceptedPins = requestAcceptedEvent.getPins();

        final TelcoRechargeData data = context.mostRecentOf(TelcoRechargeData.class).get();

        List<String> waitingAcceptance = data.requestedPins(context).stream()
                .filter(pin -> !acceptedPins.contains(pin))
                .collect(toList());

        final BaseTelcoState nextState = waitingAcceptance.isEmpty() ?
                context.stateFactory().getStateByClass(WaitingForConfirmationState.class) : this;

        return Transition.To(nextState, requestAcceptedEvent, context);
    }
}
