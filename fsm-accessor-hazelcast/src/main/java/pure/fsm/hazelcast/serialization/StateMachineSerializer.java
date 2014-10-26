package pure.fsm.hazelcast.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@SuppressWarnings("unchecked")
public class StateMachineSerializer implements StreamSerializer<StateMachine> {

    private final ObjectMapper mapper;
    private StateFactory stateFactory;

    public StateMachineSerializer() {
        mapper = new ObjectMapper(new SmileFactory());
        mapper.registerModule(new JSR310Module());
    }

    public void registerModule(Module module) {
        mapper.registerModule(module);
    }

    @Override
    public void write(ObjectDataOutput out, StateMachine sm) throws IOException {
        try {
            Map<String, Object> state = writeStateMachine(sm);
            byte[] data = mapper.writeValueAsBytes(state);
            out.write(data);
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }

    private Map<String, Object> writeStateMachine(StateMachine sm) throws JsonProcessingException {
        Map<String, Object> state = newHashMap();

        state.put("stateMachineId", sm.getStateMachineId());
        state.put("currentState", sm.getCurrentState().getClass().getName());

        writePrevious(sm, state);
        writeContext(sm, state);

        return state;
    }

    private void writeContext(StateMachine sm, Map<String, Object> state) throws JsonProcessingException {
        state.put("contextClass", sm.getContext().getClass().getName());
        state.put("context", mapper.writeValueAsBytes(sm.getContext()));
    }

    private void writePrevious(StateMachine sm, Map<String, Object> state) throws JsonProcessingException {
        if (sm.previous().isPresent()) {
            state.put("previous", writeStateMachine((StateMachine) sm.previous().get()));
        } else {
            state.put("previous", null);
        }
    }

    @Override
    public StateMachine read(ObjectDataInput in) throws IOException {
        try {
            Map<String, Object> state = mapper.readValue((InputStream) in, Map.class);
            return readStateMachine(state);
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }

    private StateMachine readStateMachine(Map<String, Object> state) throws Exception {
        String stateMachineId = (String) state.get("stateMachineId");
        State currentState = readCurrentState(state);
        StateMachine previous = readPrevious(state);
        Context context = readContext(state);

        return new StateMachine(stateMachineId, currentState, context, previous);
    }

    private StateMachine readPrevious(Map<String, Object> state) throws Exception {
        Map<String, Object> previousState = (Map<String, Object>) state.get("previous");
        return (previousState != null) ? readStateMachine(previousState) : null;
    }

    private State readCurrentState(Map<String, Object> state) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String stateName = (String) state.get("currentState");
        Class<? extends State> stateClass = (Class<? extends State>) Class.forName(stateName);
        return stateFactory.getStateByClass(stateClass);
    }

    private Context readContext(Map<String, Object> state) throws ClassNotFoundException, IOException {
        String contextClassName = (String) state.get("contextClass");
        Class<? extends Context> contextClass = (Class<? extends Context>) Class.forName(contextClassName);
        byte[] contextByteArray = (byte[]) state.get("context");
        return mapper.readValue(contextByteArray, contextClass);
    }

    @Override
    public int getTypeId() {
        return 10;
    }

    @Override
    public void destroy() {
    }

    public void setStateFactory(StateFactory stateFactory) {
        this.stateFactory = stateFactory;
    }
}
