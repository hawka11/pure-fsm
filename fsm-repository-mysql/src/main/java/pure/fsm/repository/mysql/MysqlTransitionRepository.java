package pure.fsm.repository.mysql;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.TransitionRepository;
import pure.fsm.core.Transition;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static pure.fsm.core.Transition.initialTransition;

public class MysqlTransitionRepository implements TransitionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(MysqlTransitionRepository.class);

    private final DBI jdbi;

    public MysqlTransitionRepository(DBI jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public Transition get(String stateMachineId) {
        return jdbi.withHandle(handle ->
                attachDao(handle).getStateMachineData(stateMachineId));
    }

    @Override
    public Set<String> getIds() {
        return jdbi.withHandle(handle ->
                attachDao(handle).getAllIds());
    }

    @Override
    public String create(Object initialState, List<Object> initialContextData) {
        return jdbi.withHandle(handle -> {
            final StateMachineDao dao = attachDao(handle);

            final String smId = dao.getNextId();

            final Transition transition = initialTransition(smId, initialState, initialContextData);

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
        public Transition getLast() {
            return attachDao(handle).getStateMachineData(stateMachineId);
        }

        @Override
        public void update(Transition next) {
            attachDao(handle).updateStateMachineData(stateMachineId, next);
        }

        @Override
        public boolean unlock() {
            try {
                attachDao(handle).unlock(stateMachineId);
                handle.close();
                return true;
            } catch (TransactionException e) {
                return false;
            }
        }

        @Override
        public boolean unlockAndRemove() {
            try {
                final StateMachineDao dao = attachDao(handle);
                dao.unlock(stateMachineId);
                dao.removeStateMachineData(stateMachineId);
                handle.close();
                return true;
            } catch (TransactionException e) {
                return false;
            }
        }
    }
}