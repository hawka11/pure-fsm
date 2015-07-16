package pure.fsm.repository.hazelcast.serialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import pure.fsm.core.Transition;

import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("unchecked")
public class TransitionSerializer implements StreamSerializer<Transition> {

    public final ObjectMapper mapper;

    public TransitionSerializer() {
        mapper = new ObjectMapper(new SmileFactory());
        mapper.registerModule(new JSR310Module());
        mapper.registerModule(new Jdk8Module());
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    public void registerModule(Module module) {
        mapper.registerModule(module);
    }

    @Override
    public void write(ObjectDataOutput out, Transition transition) throws IOException {
        try {
            byte[] data = mapper.writeValueAsBytes(transition);
            out.write(data);
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }

    @Override
    public Transition read(ObjectDataInput in) throws IOException {
        try {
            return mapper.readValue((InputStream) in, Transition.class);
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }

    @Override
    public int getTypeId() {
        return 10;
    }

    @Override
    public void destroy() {
    }
}
