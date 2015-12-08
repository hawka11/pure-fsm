package pure.fsm.core.transition;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pure.fsm.core.Transition;
import pure.fsm.core.fixture.PinRechargedContext;
import pure.fsm.core.fixture.TestEvent.RechargeEvent;
import pure.fsm.core.fixture.TestInitialContext;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static pure.fsm.core.Transition.initialTransition;
import static pure.fsm.core.fixture.TestState.INITIAL_STATE;
import static pure.fsm.core.fixture.TestState.RECHARGE_REQUESTED_STATE;

@RunWith(MockitoJUnitRunner.class)
public class InitialTransitionTest {

    private Transition initialTransition;
    private Transition transitioned;

    @Before
    public void beforeEach() {
        initialTransition = initialTransition("111", INITIAL_STATE, newArrayList(new TestInitialContext("data")));

        transitioned = initialTransition.setNextTransition(Transition.To(
                RECHARGE_REQUESTED_STATE, new RechargeEvent(),
                initialTransition.getContext().appendState(new PinRechargedContext())));
    }

    @Test
    public void shouldRetrieveInitialTransitionWhenTransitionIsInitial() {
        final Transition calculatedInitial = InitialTransition.initialTransition(initialTransition);
        assertThat(calculatedInitial, notNullValue());
        assertThat(calculatedInitial, equalTo(initialTransition));
    }

    @Test
    public void shouldRetrieveInitialTransitionWhenTransitionIsSubsequent() {
        final Transition calculatedInitial = InitialTransition.initialTransition(transitioned);
        assertThat(calculatedInitial, notNullValue());
        assertThat(calculatedInitial, equalTo(initialTransition));
    }
}