package simple.fsm.hazelcast.resource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.google.common.collect.Sets;
import com.hazelcast.core.HazelcastInstance;
import simple.fsm.hazelcast.resource.DistributedLockResource;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class DistributedLockResourceDeserializer extends StdDeserializer<DistributedLockResource> {

    private final HazelcastInstance hazelcastInstance;

    public DistributedLockResourceDeserializer(HazelcastInstance hazelcastInstance) {
        super(DistributedLockResource.class);
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public DistributedLockResource deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        jp.nextToken();
        String setName = jp.getValueAsString();

        Set<String> keysToLock = newHashSet();
        jp.nextToken();
        jp.nextToken();
        while (jp.nextToken() != JsonToken.END_ARRAY) {
            keysToLock.add(jp.getValueAsString());
        }

        jp.nextToken();

        return new DistributedLockResource(hazelcastInstance, setName, keysToLock);
    }
}
