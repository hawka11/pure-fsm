package pure.fsm.inmemory.accessor;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;
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

    private final ConcurrentHashMap<String, StateMachine> stateMachineByStateMachineId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> lockByStateMachineId = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public String create(State initialState, Context context) {
        String id = String.valueOf(idGenerator.getAndIncrement());
        StateMachine stateMachine = new StateMachine(id, initialState, context);
        stateMachineByStateMachineId.put(id, stateMachine);
        lockByStateMachineId.put(id, new ReentrantLock());
        return id;
    }

    @Override
    public StateMachine get(String stateMachineId) {

        return stateMachineByStateMachineId.get(stateMachineId);
    }

    @Override
    public Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        try {
            ReentrantLock reentrantLock = lockByStateMachineId.get(stateMachineId);
            if (reentrantLock != null && reentrantLock.tryLock(timeout, timeUnit)) {
                Lock lock = new Lock() {
                    @Override
                    public StateMachine getStateMachine() {
                        return stateMachineByStateMachineId.get(stateMachineId);
                    }

                    @Override
                    public void update(StateMachine newStateMachine) {
                        stateMachineByStateMachineId.put(stateMachineId, newStateMachine);
                    }

                    @Override
                    public boolean unlock() {
                        lockByStateMachineId.get(stateMachineId).unlock();
                        return true;
                    }

                    @Override
                    public boolean unlockAndRemove() {
                        unlock();
                        stateMachineByStateMachineId.remove(stateMachineId);
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
        return ImmutableSet.copyOf(stateMachineByStateMachineId.keySet());
    }
}
