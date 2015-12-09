package pure.fsm.example.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.example.user.domain.TelcoRechargeData;
import pure.fsm.example.user.domain.event.RequestAcceptedEvent;
import pure.fsm.example.user.domain.event.RequestPinEvent;
import pure.fsm.example.user.infra.TelcoGateway;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static pure.fsm.example.user.domain.state.WaitingForConfirmationState.WAITING_FOR_CONFIRMATION_STATE;

public class InitialState {

    private static final Logger LOG = LoggerFactory.getLogger(InitialState.class);

    public final static InitialState INITIAL_STATE = new InitialState();

    public EventProcessor init(DistributedResourceFactory resourceFactory, TelcoGateway telcoGateway) {
        return new EventProcessor(resourceFactory, telcoGateway);
    }

    public static class EventProcessor extends BaseTelcoState {

        private final TelcoGateway telcoGateway;

        private EventProcessor(DistributedResourceFactory resourceFactory, TelcoGateway telcoGateway) {
            super(resourceFactory);
            this.telcoGateway = telcoGateway;
        }

        @Override
        public Transition visit(Transition last, RequestPinEvent event) {
            List<String> pins = event.getPins();
            final Context context = last.getContext();

            pins.stream().forEach(pin -> context.addCanUnlock(resourceFactory().tryLock("LOCKED_PINS", pin)));

            telcoGateway.requestPinRecharge(context.stateMachineId(), pins);

            return Transition.To(this, event, context);
        }

        @Override
        public Transition visit(Transition last, RequestAcceptedEvent event) {
            final List<String> acceptedPins = event.getPins();
            final Context context = last.getContext();

            final TelcoRechargeData data = context.mostRecentOf(TelcoRechargeData.class).get();

            List<String> waitingAcceptance = data.requestedPins(context).stream()
                    .filter(pin -> !acceptedPins.contains(pin))
                    .collect(toList());

            final Object nextState = waitingAcceptance.isEmpty() ? WAITING_FOR_CONFIRMATION_STATE : this;

            return Transition.To(nextState, event, context);
        }
    }
}