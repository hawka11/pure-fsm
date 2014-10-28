package pure.fsm.core;

import pure.fsm.core.state.State;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public interface Context {

    String getStateMachineId();

    void init(String stateMachineId, State state);

    State getCurrentState();

    LocalDateTime getTransitioned();

    void addResource(Resource resource);

    Set<Resource> getResources();

    void unlockResources();

    Exception getException();

    void setException(Exception e);

    String getMessage();

    void setMessage(String msg);

    Context transition(State newState);

    Optional<Context> previous();
}
