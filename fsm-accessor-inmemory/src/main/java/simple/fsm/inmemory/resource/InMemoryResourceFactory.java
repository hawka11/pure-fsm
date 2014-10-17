package simple.fsm.inmemory.resource;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class InMemoryResourceFactory {

    public InMemoryLockResource tryLock(String setName, String... keysToLock) {
        return tryLock(setName, newHashSet(keysToLock));
    }

    public InMemoryLockResource tryLock(String setName, Set<String> keysToLock) {
        InMemoryLockResource resource = new InMemoryLockResource(setName, keysToLock);
        resource.tryLock();
        return resource;
    }
}
