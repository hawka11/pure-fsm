package pure.fsm.core.repository;

import pure.fsm.core.Transition;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface StateMachineRepository {

    Transition get(String stateMachineId);

    Set<String> getAllIds();

    Set<String> getInProgressIds();

    String create(State initialState, Class<? extends StateFactory> stateFactory, List<Object> initialContextData);

    Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit);

    interface Lock {

        Transition getLatestTransition();

        void update(Transition newTransition);

        boolean unlock();

        boolean unlockAndRemove();
    }
}
