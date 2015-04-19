package pure.fsm.inmemory.resource;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.context.CanUnlockContext;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

public class InMemoryLockResource implements CanUnlockContext {

    private final Logger LOG = LoggerFactory.getLogger(InMemoryLockResource.class);

    private final String setName;
    private final Set<String> lockedKeys;

    InMemoryLockResource(String setName, Set<String> lockedKeys) {
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

    private void doWithinLock(Function<Set<Object>, Void> function) {
        Lock lock = null;
        lock.lock();
        try {
            Set<Object> distributedSet = null;
            function.apply(distributedSet);
        } finally {
            lock.unlock();
        }
    }
}
