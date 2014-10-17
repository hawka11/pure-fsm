package simple.fsm.telcohazelcast.state;

import simple.fsm.core.state.State;
import simple.fsm.hazelcast.resource.DistributedResourceFactory;
import simple.fsm.telco.TelcoRechargeContext;
import simple.fsm.telco.event.RequestRechargeEvent;
import simple.fsm.telco.state.RechargeRequestedState;

import java.math.BigDecimal;
import java.util.Set;

public class HzInitialState extends BaseHzTelcoState {

    HzInitialState(DistributedResourceFactory factory) {
        super(factory);
    }

    @Override
    public State visit(TelcoRechargeContext context, RequestRechargeEvent requestRechargeEvent) {

        System.out.println("In InitialState, processing RequestRechargeEvent event ");

        BigDecimal rechargeAmount = requestRechargeEvent.getAmount();
        //telcoClientRepository.startRechargeProcess(rechargeAmount);

        //lock pin in distributed lock set, and represent that as a locked pin resource.
        Set<String> pinsToLock = requestRechargeEvent.getPinsToLock();
        context.addResource(getDistributedResourceFactory().tryLock("MyLockedPins", pinsToLock));

        return factory().getStateByClass(RechargeRequestedState.class);
    }
}
