package simple.fsm.optus.state;

import simple.fsm.core.state.State;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optus.event.CancelRechargeEvent;
import simple.fsm.optus.event.RechargeAcceptedEvent;

import java.math.BigDecimal;

public class RechargeRequestedState extends BaseOptusState {

    @Override
    public State visit(OptusRechargeContext context, CancelRechargeEvent cancelRechargeEvent) {
        BigDecimal rechargeAmount = context.getAmount();

        //optusClientRepository.cancelRechargeProcess(rechargeAmount);

        return new RechargedCanceledFinalState();
    }

    @Override
    public State visit(OptusRechargeContext context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        //mark context as complete
        return new RechargedCompleteFinalState();
    }
}
