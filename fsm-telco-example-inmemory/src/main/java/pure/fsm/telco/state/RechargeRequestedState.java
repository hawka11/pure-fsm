package pure.fsm.telco.state;

import pure.fsm.core.Transition;
import pure.fsm.telco.TelcoRechargeContext;
import pure.fsm.telco.event.CancelRechargeEvent;
import pure.fsm.telco.event.RechargeAcceptedEvent;
import pure.fsm.telco.guard.Guard;

import static com.google.common.collect.Lists.newArrayList;
import static pure.fsm.core.context.MostRecentContext.mostRecentOf;
import static pure.fsm.core.context.MessageContext.withMessage;

public class RechargeRequestedState extends BaseTelcoState {

    private final Guard guard;

    public RechargeRequestedState(Guard allPinsLockedGuard) {
        this.guard = allPinsLockedGuard;
    }

    @Override
    public Transition visit(Transition transition, CancelRechargeEvent cancelRechargeEvent) {

        System.out.println("In RechargeRequestedState, processing CancelRechargeEvent event ");

        //telcoClientRepository.cancelRechargeProcess();

        return transition.transitionTo(transition.stateFactory().userCanceled(transition), cancelRechargeEvent);
    }

    @Override
    public Transition visit(Transition transition, RechargeAcceptedEvent rechargeAcceptedEvent) {
        System.out.println("In RechargeRequestedState, processing RechargeAcceptedEvent event ");

        mostRecentOf(transition, TelcoRechargeContext.class).get()
                .addAcceptedPin(rechargeAcceptedEvent.getAcceptedPin());

        if (guard.isSatisfied(transition)) {
            return transition
                    .transitionTo(transition.stateFactory().successFinalState(),
                            rechargeAcceptedEvent,
                            newArrayList(withMessage("RECHARGE_ACCEPTED")));
        } else {
            //stay in current state, until all RechargeAcceptedEvent's have been received
            return transition.transitionTo(this, rechargeAcceptedEvent);
        }
    }
}
