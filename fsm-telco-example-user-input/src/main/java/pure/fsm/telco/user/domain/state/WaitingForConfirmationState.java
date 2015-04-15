package pure.fsm.telco.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.user.domain.TelcoRechargeData;
import pure.fsm.telco.user.domain.event.ConfirmPinEvent;

import java.time.LocalDateTime;

import static pure.fsm.core.Transition.transition;
import static pure.fsm.core.context.MostRecentTrait.mostRecentOf;
import static pure.fsm.core.context.MostRecentTrait.mostRecentTransition;

public class WaitingForConfirmationState extends BaseTelcoState {

    private static final Logger LOG = LoggerFactory.getLogger(WaitingForConfirmationState.class);

    WaitingForConfirmationState(DistributedResourceFactory resourceFactory) {
        super(resourceFactory);
    }

    @Override
    public Transition accept(Context context, ConfirmPinEvent confirmPinEvent) {

        LOG.info("confirming pin [{}]", confirmPinEvent.getPin());

        final TelcoRechargeData data = mostRecentOf(context, TelcoRechargeData.class).get();

        data.addConfirmedPin(confirmPinEvent.getPin());

        if (data.allPinsConfirmed(context)) {
            LOG.info("all pins confirmed, transitioning to successful final state");
            return context.transition(context.stateFactory().successFinalState(), confirmPinEvent);
        } else {
            LOG.info("still waiting for more pins to confirm, transitioning back to current state");
            return transition(this, context);
        }
    }

    @Override
    protected LocalDateTime getTimeoutDateTime(Context context) {
        //when waiting for user input, timeout can be alot longer than default.
        return mostRecentTransition(context).transitioned.plusSeconds(30);
    }
}
