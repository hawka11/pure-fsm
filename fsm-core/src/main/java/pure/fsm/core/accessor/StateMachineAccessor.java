package pure.fsm.core.accessor;

import pure.fsm.core.state.State;
import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;

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

        boolean unlockAndRemove();
    }
}
