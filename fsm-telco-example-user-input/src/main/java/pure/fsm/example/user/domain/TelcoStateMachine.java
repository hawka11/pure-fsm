package pure.fsm.example.user.domain;

import pure.fsm.core.FinalState;
import pure.fsm.core.StateMachine;
import pure.fsm.example.user.domain.event.TelcoEvent;
import pure.fsm.example.user.infra.TelcoGateway;
import pure.fsm.repository.hazelcast.resource.DistributedResourceFactory;

import static pure.fsm.core.unlock.UnlockContexts.unlockContexts;
import static pure.fsm.example.user.domain.state.InitialState.INITIAL_STATE;
import static pure.fsm.example.user.domain.state.WaitingForConfirmationState.WAITING_FOR_CONFIRMATION_STATE;

public class TelcoStateMachine extends StateMachine<TelcoEvent> {

    public TelcoStateMachine(DistributedResourceFactory resourceFactory, TelcoGateway telcoGateway) {

        when(INITIAL_STATE, (last, event) -> INITIAL_STATE.init(resourceFactory, telcoGateway).handle(last, event));

        when(WAITING_FOR_CONFIRMATION_STATE, (last, event) -> WAITING_FOR_CONFIRMATION_STATE.init(resourceFactory).handle(last, event));

        onTransition(Object.class, FinalState.class, t -> unlockContexts(t.getContext()));
    }
}
