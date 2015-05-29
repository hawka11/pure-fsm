package pure.fsm.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pure.fsm.core.context.CanUnlock;
import pure.fsm.core.context.InitialContext;
import pure.fsm.core.fixture.TestAlternateContext;
import pure.fsm.core.fixture.TestCanUnlock;
import pure.fsm.core.fixture.TestEvent;
import pure.fsm.core.fixture.TestInitialContext;
import pure.fsm.core.fixture.TestNonFinalState;
import pure.fsm.core.fixture.TestStateFactory;
import pure.fsm.core.state.State;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static pure.fsm.core.StateFactoryRegistration.registerStateFactory;
import static pure.fsm.core.Transition.initialTransition;

@RunWith(MockitoJUnitRunner.class)
public class TransitionTest {

    @Mock
    private State initialState;

    private Transition initialTransition;
    private Transition transitioned;
    private Context testInitialContext;

    @Before
    public void beforeEach() {
        registerStateFactory(new TestStateFactory());

        testInitialContext = null;
        initialTransition = initialTransition("111", initialState, TestStateFactory.class, newArrayList(testInitialContext));
        //transitioned = initialTransition.transitionTo(new TestNonFinalState(), new TestEvent(), newArrayList(new TestAlternateContext()));
    }

    @Test
    public void shouldContainInitialBuiltInValues() {
        final List<InitialContext> testContexts = testInitialContext.getContextsOfType(InitialContext.class);
        assertThat(testContexts.size(), equalTo(1));
        assertThat(initialTransition.getEvent(), equalTo(""));
        assertThat(initialTransition.getState(), nullValue());
        assertThat(initialTransition.getTransitioned(), notNullValue());
    }

    @Test
    public void shouldContainInitialUserValues() {
        final List<TestInitialContext> testContexts = testInitialContext.getContextsOfType(TestInitialContext.class);
        assertThat(testContexts.size(), equalTo(1));
    }

    @Test
    public void shouldTransitionedFieldsBeCorrect() {
        assertThat(transitioned.getEvent(), equalTo("pure.fsm.core.fixture.TestEvent"));
        assertThat(transitioned.getState(), notNullValue());
        assertThat(transitioned.getState().getClass(), equalTo(TestNonFinalState.class));
        assertThat(transitioned.getTransitioned(), notNullValue());
    }

    @Test
    public void shouldMutateAndAppendONLYCanUnlockContextToSupportUnlockingWithinFinalStateOnEnter() {
        testInitialContext.addCanUnlock(new TestCanUnlock());
        final List<CanUnlock> contexts = testInitialContext.getContextsOfType(CanUnlock.class);
        assertThat(contexts.size(), equalTo(1));
    }

    @Test
    public void shouldNormalContextsOnlyExistInTransitionedAndNotMutateExisting() {
        assertThat(testInitialContext.getContextsOfType(TestAlternateContext.class).size(), equalTo(0));
        assertThat(testInitialContext.getContextsOfType(TestAlternateContext.class).size(), equalTo(1));
    }
}