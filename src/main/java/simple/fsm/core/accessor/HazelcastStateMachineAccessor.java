package simple.fsm.core.accessor;

import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.state.State;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//TODO...example
public class HazelcastStateMachineAccessor implements StateMachineAccessor {

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
        return null;
    }

    @Override
    public Set<String> getAllIds() {
        return null;
    }
}
