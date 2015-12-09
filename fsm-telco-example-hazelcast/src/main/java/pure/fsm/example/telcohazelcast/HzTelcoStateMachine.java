package pure.fsm.example.telcohazelcast;

import pure.fsm.core.FinalState;
import pure.fsm.core.StateMachine;
import pure.fsm.example.inmemory.event.TelcoEvent;
import pure.fsm.example.inmemory.guard.Guard;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;

import static pure.fsm.core.unlock.UnlockContexts.unlockContexts;
import static pure.fsm.example.inmemory.state.RechargeRequestedState.RECHARGE_REQUESTED_STATE;
import static pure.fsm.example.telcohazelcast.state.HzInitialState.INITIAL_STATE;

public class HzTelcoStateMachine extends StateMachine<TelcoEvent> {

    public HzTelcoStateMachine(Guard guard, DistributedResourceFactory factory) {

        when(INITIAL_STATE, (last, event) -> INITIAL_STATE.init(factory).handle(last, event));

        when(RECHARGE_REQUESTED_STATE, (last, event) -> RECHARGE_REQUESTED_STATE.init(guard).handle(last, event));


        onTransition(RECHARGE_REQUESTED_STATE.getClass(), FinalState.class,
                transition -> unlockContexts(transition.getContext()));
    }
}
