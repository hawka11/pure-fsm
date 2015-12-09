package pure.fsm.example.inmemory.state;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.example.inmemory.event.RequestRechargeEvent;

import java.math.BigDecimal;
import java.util.Set;

public class InitialState extends BaseTelcoState {

    public static final InitialState INITIAL_STATE = new InitialState();

    @Override
    public Transition visit(Transition last, RequestRechargeEvent requestRechargeEvent) {

        System.out.println("In InitialState, processing RequestRechargeEvent event ");

        BigDecimal rechargeAmount = requestRechargeEvent.getAmount();
        Set<String> pinsToLock = requestRechargeEvent.getPinsToLock();

        final Context updatedContext = last.getContext().appendState(new LockedPinResource(pinsToLock));

        pinsToLock.stream().forEach(pin -> {
            //telcoClientRepository.startRechargeProcess(rechargeAmount, pin);
        });

        //lock pin in distributed lock set, and represent that as a locked pin resource.
        return Transition.To(RechargeRequestedState.class, requestRechargeEvent, updatedContext);
    }
}
