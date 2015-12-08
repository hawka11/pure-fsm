package pure.fsm.core;

import pure.fsm.core.Transition;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface StateMachineRepository {

    Transition get(String stateMachineId);

    Set<String> getAllIds();

    Set<String> getInProgressIds();

    String create(Object initialState, List<Object> initialContextData);

    Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit);

    interface Lock {

        Transition getLastTransition();

        void update(Transition newTransition);

        /**
         * Must be idempotent
         */
        boolean unlock();

        /**
         * Must be idempotent
         */
        boolean unlockAndRemove();
    }
}
