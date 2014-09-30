package simple.fsm.core.accessor;

import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.state.State;

import java.util.List;
import java.util.concurrent.TimeUnit;

//TODO...example
public class HazelcastStateMachineAccessor implements StateMachineAccessor {

    @Override
    public String create(State initialState, Context context) {
        return null;
    }

    @Override
    public StateMachine getLatest(String stateMachineId) {
        return null;
    }

    @Override
    public StateMachine tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public boolean unlock(String stateMachineId) {
        return false;
    }

    @Override
    public List<StateMachine> getAllUnlocked() {
        return null;
    }

    @Override
    public void update(String stateMachineId, StateMachine newStateMachine) {

    }
}
