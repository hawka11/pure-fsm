package simple.fsm.hazelcast.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.ByteArraySerializer;

import java.io.IOException;
import java.io.InputStream;

public abstract class BaseJacksonSerializer<T> implements ByteArraySerializer<T> {

    private final Class<T> klass;
    protected ObjectMapper mapper = new ObjectMapper(new SmileFactory());

    public BaseJacksonSerializer( Class<T> klass) {
        this.klass = klass;
    }

    public static <T> BaseJacksonSerializer seralizer(final int typeid, Class<T> klass) {
        return new BaseJacksonSerializer<T>(klass) {
            @Override
            public int getTypeId() {
                return typeid;
            }
        };
    }

    public void write(ObjectDataOutput out, T object) throws IOException {
        byte[] data = mapper.writeValueAsBytes(object);
        out.write(data);
    }

    public T read(ObjectDataInput in) throws IOException {
        return mapper.readValue((InputStream) in, klass);
    }



    public void destroy() {
    }

    @Override
    public byte[] write(T customer) throws IOException {
        return mapper.writeValueAsBytes(customer);
    }

    @Override
    public T read(byte[] bytes) throws IOException {
        return mapper.readValue(bytes, klass);
    }
}
