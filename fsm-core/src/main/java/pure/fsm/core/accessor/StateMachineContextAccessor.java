package pure.fsm.core.accessor;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface StateMachineContextAccessor {

    Transition get(String stateMachineId);

    Set<String> getAllIds();

    Set<String> getAllNonFinalIds();

    String create(State initialState, Class<? extends StateFactory> stateFactory, List<Context> initialTraits);

    Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit);

    static interface Lock {

        Transition getTransition();

        void update(Transition newStateMachine);

        boolean unlock();

        boolean unlockAndRemove();
    }
}
