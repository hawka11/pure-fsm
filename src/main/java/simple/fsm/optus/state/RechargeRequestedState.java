package simple.fsm.optus.state;

import simple.fsm.core.state.State;
import simple.fsm.core.state.SuccessFinalState;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optus.event.CancelRechargeEvent;
import simple.fsm.optus.event.RechargeAcceptedEvent;

import static simple.fsm.core.state.SuccessFinalState.userCanceled;

public class RechargeRequestedState extends BaseOptusState {

    @Override
    public State visit(OptusRechargeContext context, CancelRechargeEvent cancelRechargeEvent) {

        System.out.println("In RechargeRequestedState, processing CancelRechargeEvent event ");

        //optusClientRepository.cancelRechargeProcess();

        return userCanceled();
    }

    @Override
    public State visit(OptusRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        System.out.println("In RechargeRequestedState, processing RechargeAcceptedEvent event ");

        //mark context as complete

        return new SuccessFinalState("RECHARGE_ACCEPTED", "Optus recharged success");
    }
}
