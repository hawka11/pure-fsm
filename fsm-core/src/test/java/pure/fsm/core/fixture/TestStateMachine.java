package pure.fsm.core.fixture;

import pure.fsm.core.StateMachine;
import pure.fsm.core.fixture.TestEvent.RechargeAcceptedEvent;
import pure.fsm.core.fixture.TestEvent.RechargeEvent;

import static pure.fsm.core.fixture.TestState.INITIAL_STATE;
import static pure.fsm.core.fixture.TestState.RECHARGE_ACCEPTED_FINAL_STATE;
import static pure.fsm.core.fixture.TestState.RECHARGE_REQUESTED_STATE;

public class TestStateMachine extends StateMachine<TestEvent> {

    public final static TestStateMachine TEST_STATE_MACHINE = new TestStateMachine();

    private TestStateMachine() {

        when(INITIAL_STATE, (last, event) -> {
            if (RechargeEvent.class.equals(event.getClass())) {
                return go(RECHARGE_REQUESTED_STATE, event, last.getContext().appendState(new PinRechargedContext()));
            } else {
                return error(event, last.getContext());
            }
        });

        when(RECHARGE_REQUESTED_STATE, (last, event) -> {
            if (RechargeAcceptedEvent.class.equals(event.getClass())) {
                return go(RECHARGE_ACCEPTED_FINAL_STATE, event, last.getContext());
            } else {
                return error(event, last.getContext());
            }
        });
    }
}
