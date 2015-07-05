package pure.fsm.core.transition;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pure.fsm.core.Transition;
import pure.fsm.core.fixture.TestInitialContext;
import pure.fsm.core.fixture.TestStateFactory;
import pure.fsm.core.state.State;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static pure.fsm.core.StateFactoryRegistration.registerStateFactory;
import static pure.fsm.core.Transition.initialTransition;

@RunWith(MockitoJUnitRunner.class)
public class InitialTransitionTest {

    @Mock
    private State initialState;

    private Transition initialTransition;
    private Transition transitioned;

    @Before
    public void beforeEach() {
        registerStateFactory(new TestStateFactory());

        initialTransition = initialTransition("111", initialState, TestStateFactory.class, newArrayList(new TestInitialContext("data")));
        //transitioned = initialTransition.transitionTo(new TestNonFinalState(), new TestEvent(), newArrayList(new TestAlternateContext()));
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