package pure.fsm.example.telcohazelcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import pure.fsm.core.Transition;
import pure.fsm.core.test.fixture.state.InitialState;
import pure.fsm.repository.hazelcast.serialization.TransitionSerializer;

import java.io.ByteArrayInputStream;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static pure.fsm.core.Transition.initialTransition;
import static pure.fsm.example.telcohazelcast.HzTelcoRechargeContext.initialTelcoRecharge;

public class HzTelcoRechargeContextTest {

    @Test
    public void test() throws Exception {
        final ObjectMapper mapper = new TransitionSerializer().mapper;

        final Transition transition = initialTransition("1", new InitialState(),
                newArrayList(initialTelcoRecharge().addAcceptedPin("333")));

        byte[] data = mapper.writeValueAsBytes(transition);

        final Transition desTransition = mapper.readValue(new ByteArrayInputStream(data), Transition.class);

        assertThat(desTransition, is(notNullValue()));
        assertThat(desTransition.getContext().getContextsOfType(HzTelcoRechargeContext.class).size(), equalTo(1));
        assertThat(desTransition.getContext().getContextsOfType(HzTelcoRechargeContext.class).get(0).getAcceptedPins().size(), equalTo(1));
    }
}