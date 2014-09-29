package simple.fsm.accessor;

import simple.fsm.StateMachine;

import java.util.concurrent.TimeUnit;

public interface StateMachineAccessor {

    StateMachine tryLock(String stateMachineId);

    StateMachine tryLock(String stateMachineId, long waitFor, TimeUnit timeUnit);

    boolean unlock(String stateMachineId);
}
