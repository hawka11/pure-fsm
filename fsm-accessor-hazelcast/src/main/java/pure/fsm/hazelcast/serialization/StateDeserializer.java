package pure.fsm.hazelcast.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;

import java.io.IOException;

public class StateDeserializer extends StdDeserializer<State> {

    private final StateFactory stateFactory;

    protected StateDeserializer(StateFactory stateFactory) {
        super(State.class);
        this.stateFactory = stateFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public State deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        try {
            String stateName = jp.readValueAs(String.class);
            Class<? extends State> stateClass = (Class<? extends State>) Class.forName(stateName);
            return stateFactory.getStateByClass(stateClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
