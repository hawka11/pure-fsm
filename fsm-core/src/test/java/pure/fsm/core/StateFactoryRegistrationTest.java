package pure.fsm.core;

import org.junit.Before;
import org.junit.Test;
import pure.fsm.core.fixture.TestStateFactory;
import pure.fsm.core.state.StateFactory;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static pure.fsm.core.StateFactoryRegistration.getStateFactory;
import static pure.fsm.core.StateFactoryRegistration.registerStateFactory;

public class StateFactoryRegistrationTest {

    private TestStateFactory stateFactory;

    @Before
    public void beforeEach() {
        stateFactory = new TestStateFactory();
    }

    @Test
    public void shouldRetrieveStateFactoryByClass() {
        registerStateFactory(stateFactory);

        final Optional<StateFactory> byClass = getStateFactory(TestStateFactory.class);
        assertTrue(byClass.isPresent());
        assertThat(byClass.get(), equalTo(stateFactory));
    }

    @Test
    public void shouldRetrieveStateFactoryByClassName() {
        registerStateFactory(stateFactory);

        final Optional<StateFactory> byClass = getStateFactory(TestStateFactory.class.getName());
        assertTrue(byClass.isPresent());
        assertThat(byClass.get(), equalTo(stateFactory));
    }

}