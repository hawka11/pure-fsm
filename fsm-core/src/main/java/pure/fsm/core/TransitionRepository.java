package pure.fsm.core;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface TransitionRepository {

    Transition get(String stateMachineId);

    Set<String> getIds();

    String create(Object initialState, List<Object> initialContextData);

    Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit);

    interface Lock {

        Transition getLast();

        void update(Transition next);

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
