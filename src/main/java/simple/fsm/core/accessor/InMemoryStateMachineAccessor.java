package simple.fsm.core.accessor;

import com.google.common.collect.ImmutableSet;
import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.state.State;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.collect.Sets.newHashSet;

public class InMemoryStateMachineAccessor implements StateMachineAccessor {

    private final AtomicLong idGenerator = new AtomicLong(1000);

    private final Set<String> lockedStateMachineIds = newHashSet();
    private final HashMap<String, StateMachine> stateMachineByStateMachineId = new HashMap<>();

    //TODO: correctly synchronise all these calls.

    //TODO: cleanup of final state state machines thread ticker

    @Override
    @SuppressWarnings("unchecked")
    public String create(State initialState, Context context) {
        String id = String.valueOf(idGenerator.getAndIncrement());
        StateMachine stateMachine = new StateMachine(id, initialState, context);
        stateMachineByStateMachineId.put(id, stateMachine);
        return id;
    }

    @Override
    public StateMachine get(String stateMachineId) {

        return stateMachineByStateMachineId.get(stateMachineId);
    }

    @Override
    public Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {

        lockedStateMachineIds.add(stateMachineId);

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
                lockedStateMachineIds.remove(stateMachineId);
                return true;
            }
        };

        return Optional.of(lock);
    }

    @Override
    public Set<String> getAllIds() {
        return ImmutableSet.copyOf(stateMachineByStateMachineId.keySet());
    }
}
