package pure.fsm.telco.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.user.domain.TelcoRechargeData;
import pure.fsm.telco.user.domain.event.ConfirmPinEvent;

import java.time.LocalDateTime;

public class WaitingForConfirmationState extends BaseTelcoState {

    private static final Logger LOG = LoggerFactory.getLogger(WaitingForConfirmationState.class);

    WaitingForConfirmationState(DistributedResourceFactory resourceFactory) {
        super(resourceFactory);
    }

    @Override
    public Transition accept(Context context, ConfirmPinEvent confirmPinEvent) {

        LOG.info("confirming pin [{}]", confirmPinEvent.getPin());

        final TelcoRechargeData data = context.mostRecentOf(TelcoRechargeData.class).get();
        final TelcoRechargeData updatedData = data.addConfirmedPin(confirmPinEvent.getPin());
        final Context updatedContext = context.appendState(updatedData);

        if (updatedData.allPinsConfirmed(updatedContext)) {
            LOG.info("all pins confirmed, transitioning to successful final state");
            return Transition.To(updatedContext.stateFactory().successFinalState(), confirmPinEvent, updatedContext);
        } else {
            LOG.info("still waiting for more pins to confirm, transitioning back to current state");
            return Transition.To(this, confirmPinEvent, updatedContext);
        }
    }

    @Override
    protected LocalDateTime getTimeoutDateTime(Transition prevTransition) {
        //when waiting for user input, timeout can be alot longer than default.
        return prevTransition.getTransitioned().plusSeconds(30);
    }
}
