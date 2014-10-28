package pure.fsm.core.context;

import pure.fsm.core.Context;
import pure.fsm.core.Resource;
import pure.fsm.core.state.State;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public abstract class BaseContext implements Context {

    private String stateMachineId;
    private final Set<Resource> resources;
    private Exception e;
    private String msg;
    private final LocalDateTime transitioned;
    private final Context previous;
    private State currentState;

    protected BaseContext(String stateMachineId, Set<Resource> resources,
                          Exception e, String msg,
                          LocalDateTime transitioned,
                          State currentState, Context previous) {
        this.stateMachineId = stateMachineId;
        this.resources = resources;
        this.e = e;
        this.msg = msg;
        this.transitioned = transitioned;
        this.currentState = currentState;
        this.previous = previous;
    }

    @Override
    public void init(String stateMachineId, State state) {
        this.stateMachineId = stateMachineId;
        this.currentState = state;
    }

    @Override
    public String getStateMachineId() {
        return stateMachineId;
    }

    @Override
    public State getCurrentState() {
        return currentState;
    }

    @Override
    public LocalDateTime getTransitioned() {
        return transitioned;
    }

    @Override
    public void addResource(Resource resource) {
        resources.add(resource);
    }

    @Override
    public void unlockResources() {
        resources.forEach(Resource::unlock);
    }

    @Override
    public Set<Resource> getResources() {
        return resources;
    }

    @Override
    public Exception getException() {
        return e;
    }

    @Override
    public void setException(Exception e) {
        this.e = e;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public void setMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public Optional<Context> previous() {
        return Optional.ofNullable(previous);
    }
}
