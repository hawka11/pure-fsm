package pure.fsm.end2end;

import pure.fsm.core.StateMachine;
import pure.fsm.core.test.fixture.event.TelcoEvent;
import pure.fsm.core.test.fixture.guard.Guard;

import static pure.fsm.core.test.fixture.state.InitialState.INITIAL_STATE;
import static pure.fsm.core.test.fixture.state.RechargeRequestedState.RECHARGE_REQUESTED_STATE;

public class TelcoStateMachine extends StateMachine<TelcoEvent> {

    public TelcoStateMachine(Guard guard) {

        when(INITIAL_STATE, INITIAL_STATE::handle);

        when(RECHARGE_REQUESTED_STATE, (last, event) -> RECHARGE_REQUESTED_STATE.init(guard).handle(last, event));
    }
}
