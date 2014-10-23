package pure.fsm.telco.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.state.State;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.user.domain.TelcoRechargeContext;
import pure.fsm.telco.user.domain.event.ConfirmPinEvent;

public class WaitingForAcceptance extends BaseTelcoState {

    private static final Logger LOG = LoggerFactory.getLogger(WaitingForAcceptance.class);

    WaitingForAcceptance(DistributedResourceFactory resourceFactory) {
        super(resourceFactory);
    }

    @Override
    public State accept(TelcoRechargeContext context, ConfirmPinEvent confirmPinEvent) {

        LOG.info("confirming pin [{}]", confirmPinEvent.getPin());

        context.addConfirmedPin(confirmPinEvent.getPin());

        if (context.allPinsConfirmed()) {
            LOG.info("all pins confirmed, transitioning to successful final state");
            return factory().successFinalState();
        } else {
            LOG.info("still waiting for more pins to confirm, transitioning back to current state");
            return this;
        }
    }
}
