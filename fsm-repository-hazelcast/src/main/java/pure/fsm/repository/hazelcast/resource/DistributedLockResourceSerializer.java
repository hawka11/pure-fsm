package pure.fsm.repository.hazelcast.resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class DistributedLockResourceSerializer extends StdSerializer<DistributedLockResource> {

    public DistributedLockResourceSerializer() {
        super(DistributedLockResource.class);
    }

    @Override
    public void serialize(DistributedLockResource value,
                          JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {

        jgen.writeStringField("setName", value.getSetName());

        jgen.writeArrayFieldStart("lockedkeys");
        for (String key : value.getLockedKeys()) {
            jgen.writeString(key);
        }
        jgen.writeEndArray();
    }

    @Override
    public void serializeWithType(DistributedLockResource value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForObject(value, jgen, DistributedLockResource.class);
        serialize(value, jgen, provider);
        typeSer.writeTypeSuffixForObject(value, jgen);
    }
}
