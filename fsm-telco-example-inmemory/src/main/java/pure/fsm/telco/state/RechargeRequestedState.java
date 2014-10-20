package pure.fsm.telco.state;

import pure.fsm.core.state.State;
import pure.fsm.telco.TelcoRechargeContext;
import pure.fsm.telco.event.CancelRechargeEvent;
import pure.fsm.telco.event.RechargeAcceptedEvent;
import pure.fsm.telco.guard.AllPinsRechargedAcceptedGuard;

public class RechargeRequestedState extends BaseTelcoState {

    private final AllPinsRechargedAcceptedGuard guard;

    public RechargeRequestedState(AllPinsRechargedAcceptedGuard guard) {
        this.guard = guard;
    }

    @Override
    public State visit(TelcoRechargeContext context, CancelRechargeEvent cancelRechargeEvent) {

        System.out.println("In RechargeRequestedState, processing CancelRechargeEvent event ");

        //telcoClientRepository.cancelRechargeProcess();

        return factory().userCanceled(context);
    }

    @Override
    public State visit(TelcoRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        System.out.println("In RechargeRequestedState, processing RechargeAcceptedEvent event ");

        context.addAcceptedPin(rechargeAcceptedEvent.getAcceptedPin());

        if (guard.isSatisfied(context)) {
            context.setMessage("RECHARGE_ACCEPTED");
            return factory().successFinalState();
        } else {
            //stay in current state, until all RechargeAcceptedEvent's have been received
            return factory().getStateByClass(getClass());
        }
    }
}
