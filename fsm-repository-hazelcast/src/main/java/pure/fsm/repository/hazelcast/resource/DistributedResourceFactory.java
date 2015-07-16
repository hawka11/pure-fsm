package pure.fsm.repository.hazelcast.resource;

import com.hazelcast.core.HazelcastInstance;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class DistributedResourceFactory {

    private final HazelcastInstance instance;

    public DistributedResourceFactory(HazelcastInstance instance) {
        this.instance = instance;
    }

    public DistributedLockResource tryLock(String setName, String... keysToLock) {
        return tryLock(setName, newHashSet(keysToLock));
    }

    public DistributedLockResource tryLock(String setName, Set<String> keysToLock) {
        DistributedLockResource resource = new DistributedLockResource(instance, setName, keysToLock);
        resource.tryLock();
        return resource;
    }
}
