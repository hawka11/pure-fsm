package pure.fsm.inmemory.accessor;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.accessor.StateMachineAccessor;
import pure.fsm.core.state.State;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryStateMachineAccessor implements StateMachineAccessor {

    private final Logger LOG = LoggerFactory.getLogger(InMemoryStateMachineAccessor.class);

    private final AtomicLong idGenerator = new AtomicLong(1000);

    private final ConcurrentHashMap<String, Context> contextByStateMachineId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> lockByStateMachineId = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public String create(State initialState, Context context) {
        String id = String.valueOf(idGenerator.getAndIncrement());

        context.init(id, initialState);

        contextByStateMachineId.put(id, context);
        lockByStateMachineId.put(id, new ReentrantLock());

        return id;
    }

    @Override
    public Context get(String stateMachineId) {

        return contextByStateMachineId.get(stateMachineId);
    }

    @Override
    public Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        try {
            ReentrantLock reentrantLock = lockByStateMachineId.get(stateMachineId);
            if (reentrantLock != null && reentrantLock.tryLock(timeout, timeUnit)) {
                Lock lock = new Lock() {
                    @Override
                    public Context getContext() {
                        return contextByStateMachineId.get(stateMachineId);
                    }

                    @Override
                    public void update(Context context) {
                        contextByStateMachineId.put(stateMachineId, context);
                    }

                    @Override
                    public boolean unlock() {
                        lockByStateMachineId.get(stateMachineId).unlock();
                        return true;
                    }

                    @Override
                    public boolean unlockAndRemove() {
                        unlock();
                        contextByStateMachineId.remove(stateMachineId);
                        lockByStateMachineId.remove(stateMachineId);
                        return true;
                    }
                };

                return Optional.of(lock);
            }

        } catch (Exception e) {
            LOG.info("Could not get lock for [{}]", stateMachineId);
        }

        return Optional.empty();
    }

    @Override
    public Set<String> getAllIds() {
        return ImmutableSet.copyOf(contextByStateMachineId.keySet());
    }
}
