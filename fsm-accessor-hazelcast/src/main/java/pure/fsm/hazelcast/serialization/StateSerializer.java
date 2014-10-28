package pure.fsm.hazelcast.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import pure.fsm.core.state.State;

import java.io.IOException;

public class StateSerializer extends StdSerializer<State> {

    protected StateSerializer() {
        super(State.class);
    }

    @Override
    public void serialize(State value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeString(value.getClass().getName());
    }
}
