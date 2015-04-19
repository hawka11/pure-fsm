package pure.fsm.telcohazelcast.state;

import pure.fsm.core.Transition;
import pure.fsm.hazelcast.resource.DistributedLockResource;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.event.RequestRechargeEvent;
import pure.fsm.telco.state.RechargeRequestedState;

import java.math.BigDecimal;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class HzInitialState extends BaseHzTelcoState {

    HzInitialState(DistributedResourceFactory factory) {
        super(factory);
    }

    @Override
    public Transition visit(Transition transition, RequestRechargeEvent requestRechargeEvent) {

        System.out.println("In InitialState, processing RequestRechargeEvent event ");

        BigDecimal rechargeAmount = requestRechargeEvent.getAmount();
        Set<String> pinsToLock = requestRechargeEvent.getPinsToLock();

        //lock pin in distributed lock set, and represent that as a locked pin resource.
        final DistributedLockResource lockResource = getDistributedResourceFactory().tryLock("MyLockedPins", pinsToLock);

        //telcoClientRepository.startRechargeProcess(rechargeAmount);

        return transition.transitionTo(RechargeRequestedState.class,
                requestRechargeEvent, newArrayList(lockResource));
    }
}
