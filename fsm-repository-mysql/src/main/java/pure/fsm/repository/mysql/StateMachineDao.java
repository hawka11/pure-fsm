package pure.fsm.repository.mysql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;
import pure.fsm.core.Transition;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public abstract class StateMachineDao implements GetHandle {

    private static final Long LOCK_SUCCEEDED = 1L;
    private static final String PURE_FSM_LOCK_PREFIX = "PURE_FSM_LOCK_";

    private final ObjectMapper objectMapper = getObjectMapper();

    private static ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JSR310Module());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        return objectMapper;
    }

    public Set<String> getAllIds() {
        return getHandle().inTransaction((conn, status) -> {
            final Query<Map<String, Object>> query = conn.createQuery("SELECT id FROM statemachine");
            final List<Map<String, Object>> list = query.list();

            return list.stream()
                    .map(i -> i.get("id"))
                    .map(Object::toString)
                    .collect(toSet());
        });
    }

    public String getNextId() {
        return getHandle().inTransaction((conn, status) -> {
            final Query<Map<String, Object>> query = conn.createQuery("SELECT id FROM next_statemachine_id FOR UPDATE");
            final Map<String, Object> first = query.first();
            final String id = first.get("id").toString();
            conn.update("UPDATE next_statemachine_id SET id = id + 1");
            return id;
        });
    }

    public void insertStateMachineData(String smId, Transition transition) {
        getHandle().inTransaction((conn, status) -> {
            final Update statement = conn.createStatement("INSERT INTO statemachine VALUES (:id, :transition)");
            statement.bind("id", smId);
            statement.bind("transition", serializeTransition(transition));
            return statement.execute();
        });
    }

    public void updateStateMachineData(String smId, Transition transition) {
        getHandle().inTransaction((conn, status) -> {
            final Update statement = conn.createStatement("UPDATE statemachine SET data = :transition WHERE id = :id");
            statement.bind("id", smId);
            statement.bind("transition", serializeTransition(transition));
            return statement.execute();
        });
    }

    public boolean removeStateMachineData(String smId) {
        return getHandle().inTransaction((conn, status) -> {
            final Update statement = conn.createStatement("DELETE FROM statemachine WHERE id = :id");
            statement.bind("id", smId);
            final int numRowsRemoved = statement.execute();
            return numRowsRemoved == 1;
        });
    }

    public boolean lock(String smId, long timeout) {
        return getHandle().inTransaction((conn, status) -> {
            final Query<Map<String, Object>> query = conn.createQuery("SELECT GET_LOCK(:lock, :timeout);");
            query.bind("lock", PURE_FSM_LOCK_PREFIX + smId);
            query.bind("timeout", timeout);
            final Long result = (Long) query.first().values().stream().findFirst().get();

            System.out.println("lock result is: " + result);
            return LOCK_SUCCEEDED.equals(result);
        });

    }

    public boolean unlock(String smId) {
        return getHandle().inTransaction((conn, status) -> {
            final Update statement = conn.createStatement("DO RELEASE_LOCK(:lock);");
            statement.bind("lock", PURE_FSM_LOCK_PREFIX + smId);
            statement.execute();
            return true;
        });
    }

    public Transition getStateMachineData(String smId) {
        return getHandle().inTransaction((conn, status) -> {
            final Query<Map<String, Object>> query = conn.createQuery("SELECT data from statemachine WHERE id = :id");
            query.bind("id", smId);
            final Map<String, Object> result = query.first();
            if (result != null) {
                return deserializeTransition((String) result.get("data"));
            }
            return null;
        });
    }

    private Transition deserializeTransition(String data) {
        try {
            return objectMapper.readValue(data, Transition.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String serializeTransition(Transition transition) {
        try {
            return objectMapper.writeValueAsString(transition);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
