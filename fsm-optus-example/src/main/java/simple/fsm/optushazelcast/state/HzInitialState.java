package simple.fsm.optushazelcast.state;

import simple.fsm.core.state.State;
import simple.fsm.hazelcast.resource.DistributedResourceFactory;
import simple.fsm.optus.OptusRechargeContext;
import simple.fsm.optus.event.RequestRechargeEvent;
import simple.fsm.optus.state.RechargeRequestedState;

import java.math.BigDecimal;

public class HzInitialState extends BaseHzOptusState {

    HzInitialState(DistributedResourceFactory factory) {
        super(factory);
    }

    @Override
    public State visit(OptusRechargeContext context, RequestRechargeEvent requestRechargeEvent) {

        System.out.println("In InitialState, processing RequestRechargeEvent event ");

        BigDecimal rechargeAmount = requestRechargeEvent.getAmount();
        //optusClientRepository.startRechargeProcess(rechargeAmount);

        //lock pin in distributed lock set, and represent that as a locked pin resource.
        context.addResource(getDistributedResourceFactory().tryLock("MyLockedPins", "555", "666"));

        return factory().getStateByClass(RechargeRequestedState.class);
    }
}
