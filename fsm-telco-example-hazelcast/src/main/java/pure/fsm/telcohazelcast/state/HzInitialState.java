package pure.fsm.telcohazelcast.state;

import pure.fsm.core.state.State;
import pure.fsm.hazelcast.resource.DistributedResourceFactory;
import pure.fsm.telco.TelcoRechargeContext;
import pure.fsm.telco.event.RequestRechargeEvent;
import pure.fsm.telco.state.RechargeRequestedState;

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
        Set<String> pinsToLock = requestRechargeEvent.getPinsToLock();

        //lock pin in distributed lock set, and represent that as a locked pin resource.
        context.addResource(getDistributedResourceFactory().tryLock("MyLockedPins", pinsToLock));

        //telcoClientRepository.startRechargeProcess(rechargeAmount);

        return factory().getStateByClass(RechargeRequestedState.class);
    }
}
