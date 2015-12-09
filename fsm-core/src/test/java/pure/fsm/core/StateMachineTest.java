package pure.fsm.core;

import org.junit.Before;
import org.junit.Test;
import pure.fsm.core.fixture.PinRechargedContext;
import pure.fsm.core.fixture.TestEvent.RechargeAcceptedEvent;
import pure.fsm.core.fixture.TestEvent.RechargeEvent;
import pure.fsm.core.fixture.TestInitialContext;
import pure.fsm.core.fixture.TestState.RechargeAcceptedFinalState;
import pure.fsm.core.fixture.TestState.RechargeRequestedState;
import pure.fsm.core.fixture.TestStateMachine;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static pure.fsm.core.Transition.initialTransition;
import static pure.fsm.core.fixture.TestState.INITIAL_STATE;
import static pure.fsm.core.fixture.TestState.RECHARGE_ACCEPTED_FINAL_STATE;
import static pure.fsm.core.fixture.TestState.RECHARGE_REQUESTED_STATE;

public class StateMachineTest {

    private Transition initialTransition;
    private TestStateMachine stateMachine;
    private List<Transition> onTransitionStack;

    @Before
    public void beforeEach() {
        onTransitionStack = newArrayList();
        stateMachine = new TestStateMachine(onTransitionStack::add);
        initialTransition = initialTransition("111", INITIAL_STATE, newArrayList(new TestInitialContext("data")));
    }

    @Test
    public void shouldRequestAndAcceptSuccessfully() {
        final Transition requested = stateMachine.handleEvent(initialTransition, new RechargeEvent());
        assertThat(requested).isNotNull();
        assertThat(requested.getState().getClass()).isEqualTo(RECHARGE_REQUESTED_STATE.getClass());

        final Optional<PinRechargedContext> pinRechagedContext = requested.getContext().mostRecentOf(PinRechargedContext.class);
        assertThat(pinRechagedContext.isPresent()).isEqualTo(true);
        assertThat(onTransitionStack.size()).isEqualTo(0);

        final Transition accepted = stateMachine.handleEvent(requested, new RechargeAcceptedEvent());
        assertThat(accepted).isNotNull();
        assertThat(accepted.getState().getClass()).isEqualTo(RECHARGE_ACCEPTED_FINAL_STATE.getClass());
        assertThat(onTransitionStack.size()).isEqualTo(1);
        assertThat(onTransitionStack.get(0).getState().getClass()).isEqualTo(RechargeAcceptedFinalState.class);
        assertThat(onTransitionStack.get(0).previous().get().getState().getClass())
                .isEqualTo(RechargeRequestedState.class);
    }
}