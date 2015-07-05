package pure.fsm.jdbi.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static pure.fsm.core.Transition.initialTransition;

public class JdbiStateMachineRepository implements pure.fsm.core.repository.StateMachineRepository {

    private final Logger LOG = LoggerFactory.getLogger(JdbiStateMachineRepository.class);

    private final StateMachineDao repository;

    public JdbiStateMachineRepository(StateMachineDao repository) {
        this.repository = repository;
    }

    @Override
    public Transition get(String stateMachineId) {
        return null;
    }

    @Override
    public Set<String> getAllIds() {
        return null;
    }

    @Override
    public Set<String> getInProgressIds() {
        return null;
    }

    @Override
    public String create(State initialState, Class<? extends StateFactory> stateFactory, List<Object> initialContextData) {
        final String smId = repository.getNextId();

        final Transition transition = initialTransition(smId, initialState, stateFactory, initialContextData);

        repository.insertStateMachineData(smId, transition);

        return smId;
    }

    @Override
    public Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        return null;
    }
}
