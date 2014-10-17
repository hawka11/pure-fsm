package simple.fsm.telco.state;

import simple.fsm.core.state.State;
import simple.fsm.telco.TelcoRechargeContext;
import simple.fsm.telco.event.RequestRechargeEvent;

import java.math.BigDecimal;

public class InitialState extends BaseTelcoState {

    @Override
    public State visit(TelcoRechargeContext context, RequestRechargeEvent requestRechargeEvent) {

        System.out.println("In InitialState, processing RequestRechargeEvent event ");

        BigDecimal rechargeAmount = requestRechargeEvent.getAmount();

        //telcoClientRepository.startRechargeProcess(rechargeAmount);

        //lock pin in distributed lock set, and represent that as a locked pin resource.
        context.addResource(new LockedPinResource());

        return factory().getStateByClass(RechargeRequestedState.class);
    }
}
