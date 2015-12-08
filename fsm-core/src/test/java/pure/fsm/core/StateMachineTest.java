package pure.fsm.core;

import org.junit.Before;
import org.junit.Test;
import pure.fsm.core.fixture.PinRechargedContext;
import pure.fsm.core.fixture.TestEvent.RechargeAcceptedEvent;
import pure.fsm.core.fixture.TestEvent.RechargeEvent;
import pure.fsm.core.fixture.TestInitialContext;

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static pure.fsm.core.FinalState.ERROR_FINAL_STATE;
import static pure.fsm.core.Transition.initialTransition;
import static pure.fsm.core.fixture.TestState.INITIAL_STATE;
import static pure.fsm.core.fixture.TestState.RECHARGE_ACCEPTED_FINAL_STATE;
import static pure.fsm.core.fixture.TestState.RECHARGE_REQUESTED_STATE;
import static pure.fsm.core.fixture.TestStateMachine.TEST_STATE_MACHINE;

public class StateMachineTest {

    private Transition initialTransition;

    @Before
    public void beforeEach() {
        initialTransition = initialTransition("111", INITIAL_STATE, newArrayList(new TestInitialContext("data")));
    }

    @Test
    public void shouldRequestAndAcceptSuccessfully() {
        final Transition requested = TEST_STATE_MACHINE.handleEvent(initialTransition, new RechargeEvent());
        assertThat(requested).isNotNull();
        assertThat(requested.getState().getClass()).isEqualTo(RECHARGE_REQUESTED_STATE.getClass());

        final Optional<PinRechargedContext> pinRechagedContext = requested.getContext().mostRecentOf(PinRechargedContext.class);
        assertThat(pinRechagedContext.isPresent()).isEqualTo(true);

        final Transition accepted = TEST_STATE_MACHINE.handleEvent(requested, new RechargeAcceptedEvent());
        assertThat(accepted).isNotNull();
        assertThat(accepted.getState().getClass()).isEqualTo(RECHARGE_ACCEPTED_FINAL_STATE.getClass());
    }

    @Test
    public void shouldErrorWhenUnknownEvent() {
        final Transition requested = TEST_STATE_MACHINE.handleEvent(initialTransition, new Object());
        assertThat(requested).isNotNull();
        assertThat(requested.getState().getClass()).isEqualTo(ERROR_FINAL_STATE.getClass());
    }
}