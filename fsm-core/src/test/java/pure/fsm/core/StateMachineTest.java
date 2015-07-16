package pure.fsm.core;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import pure.fsm.core.fixture.TestAlternateContext;
import pure.fsm.core.fixture.TestEvent;
import pure.fsm.core.fixture.TestInitialContext;
import pure.fsm.core.fixture.TestNonFinalState;
import pure.fsm.core.fixture.TestStateFactory;
import pure.fsm.core.state.State;

import static com.google.common.collect.Lists.newArrayList;
import static pure.fsm.core.StateFactoryRegistration.registerStateFactory;
import static pure.fsm.core.StateMachine.STATE_MACHINE_INSTANCE;
import static pure.fsm.core.Transition.initialTransition;

public class StateMachineTest {

    @Mock
    private State initialState;

    private Transition initialTransition;
    private Transition transitioned;

    @Before
    public void beforeEach() {
        final TestStateFactory stateFactory = new TestStateFactory();
        registerStateFactory(stateFactory);

        initialTransition = initialTransition("111", initialState, TestStateFactory.class, newArrayList(new TestInitialContext("data")));
        transitioned = initialTransition.setNextTransition(Transition.To(
                new TestNonFinalState(), new TestEvent(),
                initialTransition.getContext().appendState(new TestAlternateContext())));
    }

    @Test
    @Ignore
    public void test() {
        final Transition transitioned = STATE_MACHINE_INSTANCE.handleEvent(initialTransition, new TestEvent());
    }
}