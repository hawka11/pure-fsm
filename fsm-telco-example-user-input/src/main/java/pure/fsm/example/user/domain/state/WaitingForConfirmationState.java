package pure.fsm.example.user.domain.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.example.user.domain.TelcoRechargeData;
import pure.fsm.example.user.domain.event.ConfirmPinEvent;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;

import java.time.LocalDateTime;

import static pure.fsm.core.FinalState.SUCCESS_FINAL_STATE;

public class WaitingForConfirmationState {

    private static final Logger LOG = LoggerFactory.getLogger(WaitingForConfirmationState.class);

    public final static WaitingForConfirmationState WAITING_FOR_CONFIRMATION_STATE = new WaitingForConfirmationState();

    public EventProcessor init(DistributedResourceFactory resourceFactory) {
        return new EventProcessor(resourceFactory);
    }

    public static class EventProcessor extends BaseTelcoState {

        private EventProcessor(DistributedResourceFactory resourceFactory) {
            super(resourceFactory);
        }

        @Override
        public Transition visit(Transition last, ConfirmPinEvent event) {
            final Context context = last.getContext();

            LOG.info("confirming pin [{}]", event.getPin());

            final TelcoRechargeData data = context.mostRecentOf(TelcoRechargeData.class).get();
            final TelcoRechargeData updatedData = data.addConfirmedPin(event.getPin());
            final Context updatedContext = context.appendState(updatedData);

            if (updatedData.allPinsConfirmed(updatedContext)) {
                LOG.info("all pins confirmed, transitioning to successful final state");
                return Transition.To(SUCCESS_FINAL_STATE, event, updatedContext);
            } else {
                LOG.info("still waiting for more pins to confirm, transitioning back to current state");
                return Transition.To(this, event, updatedContext);
            }
        }

        @Override
        protected boolean isTimeout(Transition last) {
            return LocalDateTime.now().isAfter(last.getTransitioned().plusSeconds(30));
        }
    }
}