package pure.fsm.jdbi.repository;

import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

public interface StateMachineIdRepository {

    @SqlQuery("UPDATE next_statemachine_id SET id = LAST_INSERT_ID(id + 1)")
    @GetGeneratedKeys
    String getNextId();
}
