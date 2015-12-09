package pure.fsm.example.inmemory;

import pure.fsm.core.StateMachine;
import pure.fsm.java.test.fixture.event.TelcoEvent;
import pure.fsm.java.test.fixture.guard.Guard;

import static pure.fsm.java.test.fixture.state.InitialState.INITIAL_STATE;
import static pure.fsm.java.test.fixture.state.RechargeRequestedState.RECHARGE_REQUESTED_STATE;

public class TelcoStateMachine extends StateMachine<TelcoEvent> {

    public TelcoStateMachine(Guard guard) {

        when(INITIAL_STATE, INITIAL_STATE::handle);

        when(RECHARGE_REQUESTED_STATE, (last, event) -> RECHARGE_REQUESTED_STATE.init(guard).handle(last, event));
    }
}
