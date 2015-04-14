package pure.fsm.hazelcast.serialization;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import pure.fsm.core.Context;

import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("unchecked")
public class ContextSerializer implements StreamSerializer<Context> {

    public final ObjectMapper mapper;

    public ContextSerializer() {
        mapper = new ObjectMapper(new SmileFactory());
        mapper.registerModule(new JSR310Module());
        mapper.registerModule(new Jdk8Module());
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    public void registerModule(Module module) {
        mapper.registerModule(module);
    }

    @Override
    public void write(ObjectDataOutput out, Context context) throws IOException {
        try {
            byte[] data = mapper.writeValueAsBytes(context);
            out.write(data);
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }

    @Override
    public Context read(ObjectDataInput in) throws IOException {
        try {
            return mapper.readValue((InputStream) in, Context.class);
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
