package pure.fsm.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pure.fsm.core.context.InitialContext;
import pure.fsm.core.fixture.PinRechargedContext;
import pure.fsm.core.fixture.TestCanUnlock;
import pure.fsm.core.fixture.TestEvent.RechargeEvent;
import pure.fsm.core.fixture.TestInitialContext;
import pure.fsm.core.unlock.CanUnlock;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static pure.fsm.core.Transition.initialTransition;
import static pure.fsm.core.fixture.TestState.INITIAL_STATE;
import static pure.fsm.core.fixture.TestState.RECHARGE_REQUESTED_STATE;

@RunWith(MockitoJUnitRunner.class)
public class TransitionTest {

    private Transition initialTransition;
    private Transition transitioned;

    @Before
    public void beforeEach() {
        initialTransition = initialTransition("111", INITIAL_STATE, newArrayList(new TestInitialContext("12344334")));
        transitioned = initialTransition.setNextTransition(Transition.To(
                RECHARGE_REQUESTED_STATE, new RechargeEvent(),
                initialTransition.getContext().appendState(new PinRechargedContext())));
    }

    @Test
    public void shouldContainInitialBuiltInValues() {
        final List<InitialContext> testContexts = initialTransition.getContext().getContextsOfType(InitialContext.class);
        assertThat(testContexts.size(), equalTo(1));
        assertThat(initialTransition.getEvent(), equalTo("InitialEvent"));
        assertThat(initialTransition.getState().getClass(), equalTo(INITIAL_STATE.getClass()));
        assertThat(initialTransition.getTransitioned(), notNullValue());
    }

    @Test
    public void shouldContainInitialUserValues() {
        final List<TestInitialContext> testContexts = initialTransition.getContext().getContextsOfType(TestInitialContext.class);
        assertThat(testContexts.size(), equalTo(1));
    }

    @Test
    public void shouldTransitionedFieldsBeCorrect() {
        assertThat(transitioned.getEvent(), equalTo("pure.fsm.core.fixture.TestEvent$RechargeEvent"));
        assertThat(transitioned.getState(), notNullValue());
        assertThat(transitioned.getState().getClass(), equalTo(RECHARGE_REQUESTED_STATE.getClass()));
        assertThat(transitioned.getTransitioned(), notNullValue());
    }

    @Test
    public void shouldMutateAndAppendONLYCanUnlockContextToSupportUnlockingWithinFinalStateOnEnter() {
        initialTransition.getContext().addCanUnlock(new TestCanUnlock());
        final List<CanUnlock> contexts = initialTransition.getContext().getContextsOfType(CanUnlock.class);
        assertThat(contexts.size(), equalTo(1));
    }

    @Test
    public void shouldNormalContextsOnlyExistInTransitionedAndNotMutateExisting() {
        assertThat(initialTransition.getContext().getContextsOfType(PinRechargedContext.class).size(), equalTo(0));
        assertThat(transitioned.getContext().getContextsOfType(PinRechargedContext.class).size(), equalTo(1));
    }
}