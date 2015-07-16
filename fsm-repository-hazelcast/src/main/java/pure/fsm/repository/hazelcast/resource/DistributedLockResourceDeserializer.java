package pure.fsm.repository.hazelcast.resource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.hazelcast.core.HazelcastInstance;

import java.io.IOException;
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
