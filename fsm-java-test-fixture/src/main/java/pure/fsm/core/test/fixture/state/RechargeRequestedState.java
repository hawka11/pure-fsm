package pure.fsm.core.test.fixture.state;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.test.fixture.TelcoRechargeContext;
import pure.fsm.core.test.fixture.event.CancelRechargeEvent;
import pure.fsm.core.test.fixture.event.RechargeAcceptedEvent;
import pure.fsm.core.test.fixture.guard.Guard;

import static pure.fsm.core.FinalState.SUCCESS_FINAL_STATE;
import static pure.fsm.core.FinalState.USER_CANCELLED_FINAL_STATE;
import static pure.fsm.core.context.ContextMessage.withMessage;

public class RechargeRequestedState {

    public static final RechargeRequestedState RECHARGE_REQUESTED_STATE = new RechargeRequestedState();

    public EventProcesor init(Guard guard) {
        return new EventProcesor(guard);
    }

    public static class EventProcesor extends BaseTelcoState {

        private final Guard guard;

        public EventProcesor(Guard guard) {
            this.guard = guard;
        }

        @Override
        public Transition visit(Transition last, CancelRechargeEvent event) {

            System.out.println("In RechargeRequestedState, processing CancelRechargeEvent event ");

            //telcoClientRepository.cancelRechargeProcess();

            return Transition.To(USER_CANCELLED_FINAL_STATE, event, last.getContext());
        }

        @Override
        public Transition visit(Transition last, RechargeAcceptedEvent event) {
            System.out.println("In RechargeRequestedState, processing RechargeAcceptedEvent event ");

            final TelcoRechargeContext withPin = last.getContext().mostRecentOf(TelcoRechargeContext.class).get()
                    .addAcceptedPin(event.getAcceptedPin());

            final Context newState = last.getContext().appendState(withPin);

            if (guard.isSatisfied(newState)) {
                return Transition.To(SUCCESS_FINAL_STATE, event,
                        newState.appendState(withMessage("RECHARGE_ACCEPTED")));
            } else {
                //stay in current state, until all RechargeAcceptedEvent's have been received
                return Transition.To(RECHARGE_REQUESTED_STATE, event, newState);

            }
        }
    }
}
