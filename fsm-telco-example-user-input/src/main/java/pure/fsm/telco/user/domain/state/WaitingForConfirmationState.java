package pure.fsm.telco.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.user.domain.TelcoRechargeData;
import pure.fsm.telco.user.domain.event.ConfirmPinEvent;

import java.time.LocalDateTime;

import static pure.fsm.core.context.MostRecentTrait.mostRecentOf;

public class WaitingForConfirmationState extends BaseTelcoState {

    private static final Logger LOG = LoggerFactory.getLogger(WaitingForConfirmationState.class);

    WaitingForConfirmationState(DistributedResourceFactory resourceFactory) {
        super(resourceFactory);
    }

    @Override
    public Transition accept(Transition transition, ConfirmPinEvent confirmPinEvent) {

        LOG.info("confirming pin [{}]", confirmPinEvent.getPin());

        final TelcoRechargeData data = mostRecentOf(transition, TelcoRechargeData.class).get();

        data.addConfirmedPin(confirmPinEvent.getPin());

        if (data.allPinsConfirmed(transition)) {
            LOG.info("all pins confirmed, transitioning to successful final state");
            return transition.transitionTo(transition.stateFactory().successFinalState(), confirmPinEvent);
        } else {
            LOG.info("still waiting for more pins to confirm, transitioning back to current state");
            return transition.transitionTo(this, confirmPinEvent);
        }
    }

    @Override
    protected LocalDateTime getTimeoutDateTime(Transition prevTransition) {
        //when waiting for user input, timeout can be alot longer than default.
        return prevTransition.getTransitioned().plusSeconds(30);
    }
}
