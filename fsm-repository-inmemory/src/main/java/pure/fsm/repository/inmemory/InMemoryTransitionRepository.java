package pure.fsm.repository.inmemory;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.TransitionRepository;
import pure.fsm.core.Transition;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import static pure.fsm.core.Transition.initialTransition;

public class InMemoryTransitionRepository implements TransitionRepository {

    private final Logger LOG = LoggerFactory.getLogger(InMemoryTransitionRepository.class);

    private final AtomicLong idGenerator = new AtomicLong(1000);

    private final ConcurrentHashMap<String, Transition> transitionByStateMachineId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> lockByStateMachineId = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public String create(Object initialState, List<Object> initialContextData) {
        String id = String.valueOf(idGenerator.getAndIncrement());

        final Transition transition = initialTransition(id, initialState, initialContextData);

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
                    public Transition getLast() {
                        return transitionByStateMachineId.get(stateMachineId);
                    }

                    @Override
                    public void update(Transition next) {
                        transitionByStateMachineId.put(stateMachineId, next);
                    }

                    @Override
                    public boolean unlock() {
                        final ReentrantLock lock = lockByStateMachineId.get(stateMachineId);

                        boolean didUnlock = false;

                        if (lock != null) {
                            try {
                                lock.unlock();
                                didUnlock = true;
                            } catch (IllegalMonitorStateException e) {
                                didUnlock = false;
                            }
                        }

                        return didUnlock;
                    }

                    @Override
                    public boolean unlockAndRemove() {
                        final boolean didUnlock = unlock();
                        transitionByStateMachineId.remove(stateMachineId);
                        lockByStateMachineId.remove(stateMachineId);
                        return didUnlock;
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
    public Set<String> getIds() {
        return ImmutableSet.copyOf(transitionByStateMachineId.keySet());
    }
}
