package simple.fsm.accessor.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.core.state.State;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class HazelcastStateMachineAccessor implements StateMachineAccessor {

    private final Logger LOG = LoggerFactory.getLogger(HazelcastStateMachineAccessor.class);

    @Override
    public String create(State initialState, Context context) {
        return null;
    }

    @Override
    public StateMachine get(String stateMachineId) {
        return null;
    }

    @Override
    public Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        java.util.concurrent.locks.Lock lock = getHazel().getLock("STATE_MACHINE-" + stateMachineId);
        lock.lock();
        try {

        } finally {
            lock.unlock();
        }
        return null;
    }

    private HazelcastInstance getHazel() {
        Config config = new Config();
        return Hazelcast.newHazelcastInstance(config);
    }

    @Override
    public Set<String> getAllIds() {
        return null;
    }
}
