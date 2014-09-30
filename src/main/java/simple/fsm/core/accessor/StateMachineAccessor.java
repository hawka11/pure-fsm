package simple.fsm.core.accessor;

import simple.fsm.core.StateMachine;

import java.util.concurrent.TimeUnit;

public interface StateMachineAccessor {

    StateMachine tryLock(String stateMachineId);

    StateMachine tryLock(String stateMachineId, long waitFor, TimeUnit timeUnit);

    boolean unlock(String stateMachineId);
}
