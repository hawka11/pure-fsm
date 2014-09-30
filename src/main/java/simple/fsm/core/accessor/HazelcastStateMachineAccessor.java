package simple.fsm.core.accessor;

import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.state.State;

import java.util.concurrent.TimeUnit;

public class HazelcastStateMachineAccessor implements StateMachineAccessor {

    @Override
    public String create(State initialState, Context context) {
        return null;
    }

    @Override
    public StateMachine tryLock(String stateMachineId) {
        return null;
    }

    @Override
    public StateMachine tryLock(String stateMachineId, long waitFor, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public boolean unlock(String stateMachineId) {
        return false;
    }
}
