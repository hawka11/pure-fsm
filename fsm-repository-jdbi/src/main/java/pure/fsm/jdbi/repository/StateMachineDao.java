package pure.fsm.jdbi.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;
import pure.fsm.core.Transition;

import java.io.IOException;
import java.util.Map;

public abstract class StateMachineDao implements GetHandle {

    private final ObjectMapper objectMapper = getObjectMapper();

    private static ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JSR310Module());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        return objectMapper;
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

    public Transition getStateMachineData(String smId) {
        return getHandle().inTransaction((conn, status) -> {
            final Query<Map<String, Object>> query = conn.createQuery("SELECT data from statemachine WHERE id = :id");
            query.bind("id", smId);
            final Map<String, Object> result = query.first();
            return deserializeTransition((String) result.get("data"));
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