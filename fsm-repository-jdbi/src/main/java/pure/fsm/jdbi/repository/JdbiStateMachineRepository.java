package pure.fsm.jdbi.repository;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.repository.StateMachineRepository;
import pure.fsm.core.state.FinalState;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toSet;
import static pure.fsm.core.Transition.initialTransition;
import static pure.fsm.core.context.InitialContext.initialContext;

public class JdbiStateMachineRepository implements StateMachineRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbiStateMachineRepository.class);

    private final DBI jdbi;

    public JdbiStateMachineRepository(DBI jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public Transition get(String stateMachineId) {
        return jdbi.withHandle(handle ->
                attachDao(handle).getStateMachineData(stateMachineId));
    }

    @Override
    public Set<String> getAllIds() {
        return jdbi.withHandle(handle ->
                attachDao(handle).getAllIds());
    }

    @Override
    public Set<String> getInProgressIds() {
        return jdbi.withHandle(handle -> {
            final StateMachineDao dao = attachDao(handle);
            return dao.getAllIds().stream()
                    .map(dao::getStateMachineData)
                    .filter(transition -> !FinalState.class.isAssignableFrom(transition.getState().getClass()))
                    .map(transition -> initialContext(transition.getContext()).stateMachineId)
                    .collect(toSet());
        });
    }

    @Override
    public String create(State initialState, Class<? extends StateFactory> stateFactory, List<Object> initialContextData) {
        return jdbi.withHandle(handle -> {
            final StateMachineDao dao = attachDao(handle);

            final String smId = dao.getNextId();

            final Transition transition = initialTransition(smId, initialState, stateFactory, initialContextData);

            dao.insertStateMachineData(smId, transition);

            return smId;
        });
    }

    @Override
    public Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        final Handle handle = jdbi.open();
        final boolean lock = attachDao(handle).lock(stateMachineId, timeUnit.toSeconds(timeout));

        if (lock) {
            return Optional.<Lock>of(new JdbiLock(handle, stateMachineId));
        } else {
            handle.close();
        }

        return Optional.<Lock>empty();
    }

    private static StateMachineDao attachDao(Handle handle) {
        return handle.attach(StateMachineDao.class);
    }

    private static class JdbiLock implements Lock {

        private final Handle handle;
        private final String stateMachineId;

        private JdbiLock(Handle handle, String stateMachineId) {
            this.handle = handle;
            this.stateMachineId = stateMachineId;
        }

        @Override
        public Transition getLatestTransition() {
            return attachDao(handle).getStateMachineData(stateMachineId);
        }

        @Override
        public void update(Transition newTransition) {
            attachDao(handle).updateStateMachineData(stateMachineId, newTransition);
        }

        @Override
        public boolean unlock() {
            attachDao(handle).unlock(stateMachineId);
            handle.close();
            return true;
        }

        @Override
        public boolean unlockAndRemove() {
            final StateMachineDao dao = attachDao(handle);
            dao.unlock(stateMachineId);
            dao.removeStateMachineData(stateMachineId);
            handle.close();
            return true;
        }
    }
}