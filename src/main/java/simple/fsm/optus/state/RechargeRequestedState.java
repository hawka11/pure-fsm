package simple.fsm.optus.state;

import simple.fsm.core.Context;
import simple.fsm.core.state.State;
import simple.fsm.optus.event.CancelRechargeEvent;
import simple.fsm.optus.event.RechargeAcceptedEvent;

public class RechargeRequestedState extends BaseOptusState {

    @Override
    public State visit(Context context, CancelRechargeEvent cancelRechargeEvent) {
        //optusClientRepository.cancelRechargeProcess();
        return new RechargedCanceledFinalState();
    }

    @Override
    public State visit(Context context, RechargeAcceptedEvent rechargeAcceptedEvent) {
        //mark context as complete
        return new RechargedCompleteFinalState();
    }
}
