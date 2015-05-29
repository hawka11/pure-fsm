package pure.fsm.hazelcast.resource;

import com.google.common.base.Preconditions;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.ISet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.context.CanUnlock;

import java.util.Set;
import java.util.function.Function;

public class DistributedLockResource implements CanUnlock {

    private final Logger LOG = LoggerFactory.getLogger(DistributedLockResource.class);

    private final HazelcastInstance hazelcastInstance;
    private final String setName;
    private final Set<String> lockedKeys;

    DistributedLockResource(HazelcastInstance hazelcastInstance, String setName, Set<String> lockedKeys) {
        this.hazelcastInstance = hazelcastInstance;
        this.setName = setName;
        this.lockedKeys = lockedKeys;
    }

    void tryLock() {
        doWithinLock(set -> {
            boolean noKeysCurrentlyLocked = lockedKeys.stream()
                    .noneMatch(set::contains);

            Preconditions.checkState(noKeysCurrentlyLocked, "There is one or more keys locked, cannot get lock");

            LOG.info("locking distributed keys [{}]", lockedKeys);

            set.addAll(lockedKeys);

            return null;
        });
    }

    @Override
    public void unlock() {
        doWithinLock(set -> {
            LOG.info("unlocking distributed keys [{}]", lockedKeys);
            set.removeAll(lockedKeys);
            return null;
        });
    }

    public String getSetName() {
        return setName;
    }

    public Set<String> getLockedKeys() {
        return lockedKeys;
    }

    private void doWithinLock(Function<ISet<Object>, Void> function) {
        ILock lock = hazelcastInstance.getLock("DistributedLockResource_" + setName);
        lock.lock();
        try {
            ISet<Object> distributedSet = hazelcastInstance.getSet(setName);
            function.apply(distributedSet);
        } finally {
            lock.unlock();
        }
    }
}
