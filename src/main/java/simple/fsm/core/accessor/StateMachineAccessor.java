package simple.fsm.core.accessor;

import simple.fsm.core.Context;
import simple.fsm.core.StateMachine;
import simple.fsm.core.state.State;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface StateMachineAccessor {

    StateMachine get(String stateMachineId);

    Set<String> getAllIds();

    String create(State initialState, Context context);

    Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit);

    static interface Lock {

        StateMachine getStateMachine();

        void update(StateMachine newStateMachine);

        boolean unlock();
    }
}
