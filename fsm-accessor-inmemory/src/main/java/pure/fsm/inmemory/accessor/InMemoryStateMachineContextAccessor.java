package pure.fsm.inmemory.accessor;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.accessor.StateMachineContextAccessor;
import pure.fsm.core.state.FinalState;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.stream.Collectors.toSet;
import static pure.fsm.core.Transition.initialTransition;

public class InMemoryStateMachineContextAccessor implements StateMachineContextAccessor {

    private final Logger LOG = LoggerFactory.getLogger(InMemoryStateMachineContextAccessor.class);

    private final AtomicLong idGenerator = new AtomicLong(1000);

    private final ConcurrentHashMap<String, Transition> transitionByStateMachineId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> lockByStateMachineId = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public String create(State initialState, Class<? extends StateFactory> stateFactory, List<Object> initialContextData) {
        String id = String.valueOf(idGenerator.getAndIncrement());

        final Transition transition = initialTransition(id, initialState, stateFactory, initialContextData);

        transitionByStateMachineId.put(id, transition);
        lockByStateMachineId.put(id, new ReentrantLock());

        return id;
    }

    @Override
    public Transition get(String stateMachineId) {

        return transitionByStateMachineId.get(stateMachineId);
    }

    @Override
    public Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        try {
            ReentrantLock reentrantLock = lockByStateMachineId.get(stateMachineId);
            if (reentrantLock != null && reentrantLock.tryLock(timeout, timeUnit)) {
                Lock lock = new Lock() {
                    @Override
                    public Transition getLatestTransition() {
                        return transitionByStateMachineId.get(stateMachineId);
                    }

                    @Override
                    public void update(Transition newTransition) {
                        transitionByStateMachineId.put(stateMachineId, newTransition);
                    }

                    @Override
                    public boolean unlock() {
                        lockByStateMachineId.get(stateMachineId).unlock();
                        return true;
                    }

                    @Override
                    public boolean unlockAndRemove() {
                        unlock();
                        transitionByStateMachineId.remove(stateMachineId);
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
        return ImmutableSet.copyOf(transitionByStateMachineId.keySet());
    }

    @Override
    public Set<String> getInProgressIds() {
        return transitionByStateMachineId.entrySet().stream()
                .filter(e -> !FinalState.class.isAssignableFrom(e.getValue().getState().getClass()))
                .map(Map.Entry::getKey)
                .collect(toSet());
    }
}
