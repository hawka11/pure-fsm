package simple.fsm.optus.state;

import simple.fsm.core.state.State;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optus.event.RequestRechargeEvent;

import java.math.BigDecimal;

public class InitialState extends BaseOptusState {

    @Override
    public State visit(OptusRechargeContext context, RequestRechargeEvent requestRechargeEvent) {

        System.out.println("In InitialState, processing RequestRechargeEvent event ");

        BigDecimal rechargeAmount = requestRechargeEvent.getAmount();

        //optusClientRepository.startRechargeProcess(rechargeAmount);

        return new RechargeRequestedState();
    }
}
