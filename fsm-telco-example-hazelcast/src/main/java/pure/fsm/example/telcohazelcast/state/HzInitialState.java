package pure.fsm.example.telcohazelcast.state;

import pure.fsm.core.Transition;
import pure.fsm.java.test.fixture.event.RequestRechargeEvent;
import pure.fsm.java.test.fixture.state.BaseTelcoState;
import pure.fsm.java.test.fixture.state.RechargeRequestedState;
import pure.fsm.repository.hazelcast.resource.DistributedLockResource;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;

import java.math.BigDecimal;
import java.util.Set;

public class HzInitialState {

    public static final HzInitialState INITIAL_STATE = new HzInitialState();

    public EventProcessor init(DistributedResourceFactory factory) {
        return new EventProcessor(factory); //need external deps
    }

    public static class EventProcessor extends BaseTelcoState {

        private final DistributedResourceFactory factory;

        public EventProcessor(DistributedResourceFactory factory) {
            this.factory = factory;
        }

        @Override
        public Transition visit(Transition last, RequestRechargeEvent event) {

            System.out.println("In InitialState, processing RequestRechargeEvent event ");

            BigDecimal rechargeAmount = event.getAmount();
            Set<String> pinsToLock = event.getPinsToLock();

            //lock pin in distributed lock set, and represent that as a locked pin resource.
            final DistributedLockResource lockResource = factory.tryLock("MyLockedPins", pinsToLock);

            //telcoClientRepository.startRechargeProcess(rechargeAmount);

            return Transition.To(RechargeRequestedState.class,
                    event, last.getContext().addCanUnlock(lockResource));
        }
    }
}
