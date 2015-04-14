package pure.fsm.telco.state;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.telco.TelcoRechargeTrait;
import pure.fsm.telco.event.CancelRechargeEvent;
import pure.fsm.telco.event.RechargeAcceptedEvent;
import pure.fsm.telco.guard.Guard;

import static pure.fsm.core.Transition.transition;
import static pure.fsm.core.context.MostRecentTrait.mostRecentOf;
import static pure.fsm.core.trait.MessageTrait.withMessage;

public class RechargeRequestedState extends BaseTelcoState {

    private final Guard guard;

    public RechargeRequestedState(Guard allPinsLockedGuard) {
        this.guard = allPinsLockedGuard;
    }

    @Override
    public Transition visit(Context context, CancelRechargeEvent cancelRechargeEvent) {

        System.out.println("In RechargeRequestedState, processing CancelRechargeEvent event ");

        //telcoClientRepository.cancelRechargeProcess();

        return context.transition(factory().userCanceled(context), cancelRechargeEvent);
    }

    @Override
    public Transition visit(Context context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        System.out.println("In RechargeRequestedState, processing RechargeAcceptedEvent event ");

        mostRecentOf(context, TelcoRechargeTrait.class).get()
                .addAcceptedPin(rechargeAcceptedEvent.getAcceptedPin());

        if (guard.isSatisfied(context)) {
            return context
                    .addTrait(withMessage("RECHARGE_ACCEPTED"))
                    .transition(factory().successFinalState(), rechargeAcceptedEvent);
        } else {
            //stay in current state, until all RechargeAcceptedEvent's have been received
            return transition(this, context);
        }
    }
}
