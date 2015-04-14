package pure.fsm.telcohazelcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import pure.fsm.core.Context;
import pure.fsm.hazelcast.serialization.ContextSerializer;

import java.io.ByteArrayInputStream;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static pure.fsm.core.Context.initialContext;
import static pure.fsm.telcohazelcast.HzTelcoRechargeTrait.initialTelcoRecharge;

public class HzTelcoRechargeTraitTest {

    @Test
    public void test() throws Exception {
        final ObjectMapper mapper = new ContextSerializer().mapper;

        final Context context = initialContext("1", null, newArrayList(initialTelcoRecharge().addAcceptedPin("333")));

        byte[] data = mapper.writeValueAsBytes(context);

        final Context desContext = mapper.readValue(new ByteArrayInputStream(data), Context.class);

        assertThat(desContext, is(notNullValue()));
        assertThat(desContext.getTraitsOf(HzTelcoRechargeTrait.class).size(), equalTo(1));
        assertThat(desContext.getTraitsOf(HzTelcoRechargeTrait.class).get(0).getAcceptedPins().size(), equalTo(1));
    }
}