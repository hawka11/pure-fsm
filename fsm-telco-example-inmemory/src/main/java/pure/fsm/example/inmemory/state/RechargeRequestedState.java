package pure.fsm.example.inmemory.state;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.transition.UserCancelled;
import pure.fsm.example.inmemory.TelcoRechargeContext;
import pure.fsm.example.inmemory.event.CancelRechargeEvent;
import pure.fsm.example.inmemory.event.RechargeAcceptedEvent;
import pure.fsm.example.inmemory.guard.Guard;

import static pure.fsm.core.context.ContextMessage.withMessage;

public class RechargeRequestedState extends BaseTelcoState {

    private final Guard guard;

    public RechargeRequestedState(Guard allPinsLockedGuard) {
        this.guard = allPinsLockedGuard;
    }

    @Override
    public Transition visit(Context context, CancelRechargeEvent cancelRechargeEvent) {

        System.out.println("In RechargeRequestedState, processing CancelRechargeEvent event ");

        //telcoClientRepository.cancelRechargeProcess();

        return UserCancelled.transitionToUserCancelled(context, cancelRechargeEvent);
    }

    @Override
    public Transition visit(Context context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        System.out.println("In RechargeRequestedState, processing RechargeAcceptedEvent event ");

        final TelcoRechargeContext withPin = context.mostRecentOf(TelcoRechargeContext.class).get()
                .addAcceptedPin(rechargeAcceptedEvent.getAcceptedPin());

        final Context newState = context.appendState(withPin);

        if (guard.isSatisfied(newState)) {
            return Transition.To(context.stateFactory().successFinalState(),
                    rechargeAcceptedEvent,
                    newState.appendState(withMessage("RECHARGE_ACCEPTED")));
        } else {
            //stay in current state, until all RechargeAcceptedEvent's have been received
            return Transition.To(this, rechargeAcceptedEvent, newState);

        }
    }
}
