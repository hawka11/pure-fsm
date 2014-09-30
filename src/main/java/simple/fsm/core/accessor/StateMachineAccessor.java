package simple.fsm.core.accessor;

import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.state.State;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface StateMachineAccessor {

    String create(State initialState, Context context);

    StateMachine get(String stateMachineId);

    StateMachine tryLock(String stateMachineId, long timeout, TimeUnit timeUnit);

    boolean unlock(String stateMachineId);

    Set<String> getAllIds();

    void update(String stateMachineId, StateMachine newStateMachine);
}
