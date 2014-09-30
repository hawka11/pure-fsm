package simple.fsm.core.accessor;

import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.state.State;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.concurrent.TimeUnit.SECONDS;

public class InMemoryStateMachineAccessor implements StateMachineAccessor {

    private final AtomicLong idGenerator = new AtomicLong(1000);

    private final Set<String> lockedStateMachineIds = newHashSet();
    private final HashMap<String, StateMachine> stateMachineByStateMachineId = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public String create(State initialState, Context context) {
        long smId = idGenerator.getAndIncrement();
        StateMachine stateMachine = new StateMachine(initialState, context);
        stateMachineByStateMachineId.put(String.valueOf(smId), stateMachine);
        return String.valueOf(smId);
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
}
