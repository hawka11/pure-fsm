package simple.fsm.accessor.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.core.state.State;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.ImmutableSet.copyOf;

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
        java.util.concurrent.locks.Lock distributedLock = getHazel().getLock("STATE_MACHINE-" + stateMachineId);

        try {
            distributedLock.tryLock(timeout, timeUnit);

            return createLock(stateMachineId, distributedLock);
        } catch (InterruptedException e) {
            LOG.warn("Could not get HZ distributed distributedLock for state machine [" + stateMachineId + "]", e);
            return Optional.empty();
        }
    }

    @Override
    public Set<String> getAllIds() {
        return copyOf(getHolderMap().keySet());
    }

    private Optional<Lock> createLock(String stateMachineId, java.util.concurrent.locks.Lock distributedLock) {
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
                distributedLock.unlock();
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

    private synchronized HazelcastInstance getHazel() {
        if (hazelcastInstance == null) {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.addAddress("127.0.0.1:5701");
            hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
        }
        return hazelcastInstance;
    }
}
