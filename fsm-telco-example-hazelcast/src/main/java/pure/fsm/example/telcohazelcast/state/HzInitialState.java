package pure.fsm.example.telcohazelcast.state;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.repository.hazelcast.resource.DistributedLockResource;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.example.inmemory.event.RequestRechargeEvent;
import pure.fsm.example.inmemory.state.RechargeRequestedState;

import java.math.BigDecimal;
import java.util.Set;

public class HzInitialState extends BaseHzTelcoState {

    HzInitialState(DistributedResourceFactory factory) {
        super(factory);
    }

    @Override
    public Transition visit(Context context, RequestRechargeEvent requestRechargeEvent) {

        System.out.println("In InitialState, processing RequestRechargeEvent event ");

        BigDecimal rechargeAmount = requestRechargeEvent.getAmount();
        Set<String> pinsToLock = requestRechargeEvent.getPinsToLock();

        //lock pin in distributed lock set, and represent that as a locked pin resource.
        final DistributedLockResource lockResource = getDistributedResourceFactory().tryLock("MyLockedPins", pinsToLock);

        //telcoClientRepository.startRechargeProcess(rechargeAmount);

        return Transition.To(RechargeRequestedState.class,
                requestRechargeEvent, context.addCanUnlock(lockResource));
    }
}