package simple.fsm.core.accessor;

import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.state.State;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Map.Entry;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class InMemoryStateMachineAccessor implements StateMachineAccessor {

    private final AtomicLong idGenerator = new AtomicLong(1000);

    private final Set<String> lockedStateMachineIds = newHashSet();
    private final HashMap<String, StateMachine> stateMachineByStateMachineId = new HashMap<>();

    //TODO: correctly syncronize all these calls.

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
    public StateMachine getSnapshot(String stateMachineId) {
        return stateMachineByStateMachineId.get(stateMachineId);
    }

    @Override
    public StateMachine tryLock(String stateMachineId) {
        return tryLock(stateMachineId, 2, SECONDS);
    }

    @Override
    public StateMachine tryLock(String stateMachineId, long waitFor, TimeUnit timeUnit) {
        lockedStateMachineIds.add(stateMachineId);
        return stateMachineByStateMachineId.get(stateMachineId);
    }

    @Override
    public boolean unlock(String stateMachineId) {
        lockedStateMachineIds.remove(stateMachineId);
        return true;
    }

    @Override
    public List<StateMachine> getAllUnlocked() {
        return stateMachineByStateMachineId.entrySet().stream()
                .filter(e -> !lockedStateMachineIds.contains(e.getKey()))
                .map(Entry::getValue)
                .collect(toList());
    }
}
