package simple.fsm.optus.state;

import simple.fsm.core.state.State;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optus.event.CancelRechargeEvent;
import simple.fsm.optus.event.RechargeAcceptedEvent;

import java.math.BigDecimal;

public class RechargeRequestedState extends BaseOptusState {

    @Override
    public State visit(OptusRechargeContext context, CancelRechargeEvent cancelRechargeEvent) {

        System.out.println("In RechargeRequestedState, processing CancelRechargeEvent event ");

        //optusClientRepository.cancelRechargeProcess();

        return new RechargedCanceledFinalState();
    }

    @Override
    public State visit(OptusRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        System.out.println("In RechargeRequestedState, processing RechargeAcceptedEvent event ");

        //mark context as complete

        return new RechargedCompleteFinalState();
    }
}
