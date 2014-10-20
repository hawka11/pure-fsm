package pure.fsm.telco.state;

import pure.fsm.core.state.State;
import pure.fsm.telco.TelcoRechargeContext;
import pure.fsm.telco.event.RequestRechargeEvent;

import java.math.BigDecimal;
import java.util.Set;

public class InitialState extends BaseTelcoState {

    @Override
    public State visit(TelcoRechargeContext context, RequestRechargeEvent requestRechargeEvent) {

        System.out.println("In InitialState, processing RequestRechargeEvent event ");

        BigDecimal rechargeAmount = requestRechargeEvent.getAmount();
        Set<String> pinsToLock = requestRechargeEvent.getPinsToLock();

        //lock pin in distributed lock set, and represent that as a locked pin resource.
        context.addResource(new LockedPinResource(pinsToLock));

        pinsToLock.stream().forEach(pin -> {
            //telcoClientRepository.startRechargeProcess(rechargeAmount, pin);
        });

        return factory().getStateByClass(RechargeRequestedState.class);
    }
}
