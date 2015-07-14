package pure.fsm.jdbi.repository;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.repository.StateMachineRepository;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static pure.fsm.core.Transition.initialTransition;

public class JdbiStateMachineRepository implements StateMachineRepository {

    private final Logger LOG = LoggerFactory.getLogger(JdbiStateMachineRepository.class);

    private final DBI jdbi;

    public JdbiStateMachineRepository(DBI jdbi) {
        this.jdbi = jdbi;
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
        return jdbi.withHandle(handle -> {
            final StateMachineDao dao = handle.attach(StateMachineDao.class);

            final String smId = dao.getNextId();

            final Transition transition = initialTransition(smId, initialState, stateFactory, initialContextData);

            dao.insertStateMachineData(smId, transition);

            return smId;
        });
    }

    @Override
    public Optional<Lock> tryLock(String stateMachineId, long timeout, TimeUnit timeUnit) {
        final Handle handle = jdbi.open();
        final StateMachineDao dao = handle.attach(StateMachineDao.class);
        final boolean lock = dao.lock(stateMachineId, timeUnit.toSeconds(timeout));

        if (lock) {
            return Optional.<Lock>of(new JdbiLock(handle, dao, stateMachineId));
        }

        return Optional.<Lock>empty();
    }

    private static class JdbiLock implements Lock {

        private final Handle handle;
        private final StateMachineDao dao;
        private final String stateMachineId;

        private JdbiLock(Handle handle, StateMachineDao dao, String stateMachineId) {
            this.handle = handle;
            this.dao = dao;
            this.stateMachineId = stateMachineId;
        }

        @Override
        public Transition getLatestTransition() {
            return dao.getStateMachineData(stateMachineId);
        }

        @Override
        public void update(Transition newTransition) {
            dao.updateStateMachineData(stateMachineId, newTransition);
        }

        @Override
        public boolean unlock() {
            dao.unlock(stateMachineId);
            handle.close();
            return true;
        }

        @Override
        public boolean unlockAndRemove() {
            dao.unlock(stateMachineId);
            dao.removeStateMachineData(stateMachineId);
            handle.close();
            return true;
        }
    }
}