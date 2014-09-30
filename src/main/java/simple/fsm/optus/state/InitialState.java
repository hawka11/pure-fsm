package simple.fsm.optus.state;

import simple.fsm.core.state.State;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optus.event.RequestRechargeEvent;

public class InitialState extends BaseOptusState {

    @Override
    public State visit(OptusRechargeContext context, RequestRechargeEvent requestRechargeEvent) {

        System.out.println("In InitialState, processing RequestRechargeEvent event ");

       //optusClientRepository.startRechargeProcess();

        return new RechargeRequestedState();
    }
}
