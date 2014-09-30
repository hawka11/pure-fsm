package simple.fsm.optus.state;

import simple.fsm.core.Context;
import simple.fsm.core.state.State;
import simple.fsm.optus.event.RequestRechargeEvent;

public class InitialState extends BaseOptusState {

    @Override
    public State visit(Context context, RequestRechargeEvent requestRechargeEvent) {

        //optusClientRepository.startRechargeProcess();

        return new RechargeRequestedState();
    }
}
