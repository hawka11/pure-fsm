package pure.fsm.jdbi.repository;

import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

import java.util.Map;

public abstract class StateMachineIdRepository implements GetHandle {

    public String getNextId() {
        return getHandle().inTransaction((conn, status) -> {
            final Query<Map<String, Object>> query = conn.createQuery("SELECT id FROM next_statemachine_id FOR UPDATE");
            final Map<String, Object> first = query.first();
            final String id = first.get("id").toString();
            conn.update("UPDATE next_statemachine_id SET id = id + 1");
            return id;
        });
    }
}
