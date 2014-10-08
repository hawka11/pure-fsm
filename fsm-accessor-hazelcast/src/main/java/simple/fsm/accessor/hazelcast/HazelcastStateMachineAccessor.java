package simple.fsm.accessor.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.core.state.State;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.ImmutableSet.copyOf;
import static java.lang.Boolean.TRUE;
import static java.time.LocalDateTime.now;

public class HazelcastStateMachineAccessor implements StateMachineAccessor {

    private final Logger LOG = LoggerFactory.getLogger(HazelcastStateMachineAccessor.class);

    private HazelcastInstance hazelcastInstance;

    @Override
    @SuppressWarnings("unchecked")
    public String create(State initialState, Context context) {

        IAtomicLong idAtomicLong = getHazel().getAtomicLong("STATE_MACHINE_ID_GENERATOR");
        String id = String.valueOf(idAtomicLong.getAndIncrement());

        StateMachine stateMachine = new StateMachine(id, initialState, context);

        getHolderMap().put(id, stateMachine);

        return id;
    }

    @Override
    public StateMachine get(String stateMachineId) {
        return getHolderMap().get(stateMachineId);
    }

    @Override
    public Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        java.util.concurrent.locks.Lock lock = getHazel().getLock("STATE_MACHINE-" + stateMachineId);

        try {
            lock.tryLock(timeout, timeUnit);

            return attemptLock(stateMachineId, timeout, timeUnit);
        } catch (InterruptedException e) {
            LOG.warn("Could not get HZ distributed lock for state machine [" + stateMachineId + "]", e);
            return Optional.empty();
        } finally {
            lock.unlock();
        }
    }

    private synchronized HazelcastInstance getHazel() {
        if (hazelcastInstance != null) {
            Config config = new Config();
            hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        }
        return hazelcastInstance;
    }

    @Override
    public Set<String> getAllIds() {
        return copyOf(getHolderMap().keySet());
    }

    private Optional<Lock> attemptLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        Optional<Lock> lock = Optional.empty();

        try {
            LocalDateTime startDateTime = now();

            while (!lock.isPresent() && now().isBefore(startDateTime.plus(timeout, ChronoUnit.MILLIS))) {
                if (!getLockMap().containsKey(stateMachineId)) {
                    getLockMap().put(stateMachineId, TRUE);
                    lock = createLock(stateMachineId);
                }

                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            LOG.warn("Could not get lock for state machine [" + stateMachineId + "]", e);
        }

        return lock;
    }

    private Optional<Lock> createLock(String stateMachineId) {
        Lock lock = new Lock() {
            @Override
            public StateMachine getStateMachine() {
                return getHolderMap().get(stateMachineId);
            }

            @Override
            public void update(StateMachine newStateMachine) {
                getHolderMap().put(stateMachineId, newStateMachine);
            }

            @Override
            public boolean unlock() {
                getLockMap().remove(stateMachineId);
                return true;
            }

            @Override
            public boolean unlockAndRemove() {
                unlock();
                getHolderMap().remove(stateMachineId);
                return true;
            }
        };

        return Optional.of(lock);
    }

    private IMap<String, StateMachine> getHolderMap() {
        return getHazel().getMap("STATE_MACHINE_HOLDER");
    }

    private IMap<String, Boolean> getLockMap() {
        return getHazel().getMap("STATE_MACHINE_LOCK");
    }
}
