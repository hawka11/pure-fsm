package simple.fsm.core.accessor;

import simple.fsm.core.StateMachine;

import java.util.concurrent.TimeUnit;

public class InMemoryStateMachineAccessor implements StateMachineAccessor {

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
