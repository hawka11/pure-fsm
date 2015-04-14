package pure.fsm.telco.state;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.telco.event.RequestRechargeEvent;

import java.math.BigDecimal;
import java.util.Set;

public class InitialState extends BaseTelcoState {

    @Override
    public Transition visit(Context context, RequestRechargeEvent requestRechargeEvent) {

        System.out.println("In InitialState, processing RequestRechargeEvent event ");

        BigDecimal rechargeAmount = requestRechargeEvent.getAmount();
        Set<String> pinsToLock = requestRechargeEvent.getPinsToLock();

        pinsToLock.stream().forEach(pin -> {
            //telcoClientRepository.startRechargeProcess(rechargeAmount, pin);
        });

        //lock pin in distributed lock set, and represent that as a locked pin resource.
        return context.addTrait(new LockedPinResource(pinsToLock))
                .transition(factory().getStateByClass(RechargeRequestedState.class), requestRechargeEvent);
    }
}
