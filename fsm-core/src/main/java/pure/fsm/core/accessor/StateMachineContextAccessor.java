package pure.fsm.core.accessor;

import pure.fsm.core.Context;
import pure.fsm.core.state.State;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface StateMachineContextAccessor {

    Context get(String stateMachineId);

    Set<String> getAllIds();

    Set<String> getAllNonFinalIds();

    String create(State initialState, Context context);

    Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit);

    static interface Lock {

        Context getContext();

        void update(Context newStateMachine);

        boolean unlock();

        boolean unlockAndRemove();
    }
}
