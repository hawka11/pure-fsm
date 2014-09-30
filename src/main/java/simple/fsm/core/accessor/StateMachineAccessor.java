package simple.fsm.core.accessor;

import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.state.State;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface StateMachineAccessor {

    public String create(State initialState, Context context);

    StateMachine getLatest(String stateMachineId);

    StateMachine tryLock(String stateMachineId);

    StateMachine tryLock(String stateMachineId, long waitFor, TimeUnit timeUnit);

    boolean unlock(String stateMachineId);

    List<StateMachine> getAllUnlocked();

    void update(String stateMachineId, StateMachine newStateMachine);
}
