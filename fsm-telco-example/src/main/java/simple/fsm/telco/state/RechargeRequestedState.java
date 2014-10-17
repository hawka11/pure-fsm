package simple.fsm.telco.state;

import simple.fsm.core.state.State;
import simple.fsm.telco.TelcoRechargeContext;
import simple.fsm.telco.event.CancelRechargeEvent;
import simple.fsm.telco.event.RechargeAcceptedEvent;

public class RechargeRequestedState extends BaseTelcoState {

    @Override
    public State visit(TelcoRechargeContext context, CancelRechargeEvent cancelRechargeEvent) {

        System.out.println("In RechargeRequestedState, processing CancelRechargeEvent event ");

        //telcoClientRepository.cancelRechargeProcess();

        return factory().userCanceled(context);
    }

    @Override
    public State visit(TelcoRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        System.out.println("In RechargeRequestedState, processing RechargeAcceptedEvent event ");

        //TODO: mark context as complete
        context.setMessage("RECHARGE_ACCEPTED");

        return factory().successFinalState();
    }
}
