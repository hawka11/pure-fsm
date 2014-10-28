package pure.fsm.core.context;

import pure.fsm.core.Context;
import pure.fsm.core.Resource;
import pure.fsm.core.state.State;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public abstract class BaseContext implements Context {

    private String stateMachineId;
    private final Set<Resource> resources;
    private Exception e;
    private String msg;
    private final LocalDateTime transitioned;
    private final Context previous;
    private State currentState;

    protected BaseContext(BaseContextBuilder baseContextBuilder) {
        stateMachineId = baseContextBuilder.stateMachineId;
        resources = baseContextBuilder.resources;
        e = baseContextBuilder.e;
        msg = baseContextBuilder.msg;
        transitioned = baseContextBuilder.transitioned;
        previous = baseContextBuilder.previous;
        currentState = baseContextBuilder.currentState;
    }

    protected static BaseContextBuilder builder() {
        return new BaseContextBuilder();
    }

    protected static BaseContextBuilder initialTransition() {
        return builder()
                .resources(newHashSet())
                .transitioned(LocalDateTime.now());
    }

    protected BaseContextBuilder transitionWith(State newState) {
        return builder()
                .stateMachineId(getStateMachineId())
                .resources(getResources())
                .e(getException())
                .msg(getMessage())
                .transitioned(LocalDateTime.now())
                .previous(this)
                .currentState(newState);
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

    protected static final class BaseContextBuilder {
        private String stateMachineId;
        private Set<Resource> resources;
        private Exception e;
        private String msg;
        private LocalDateTime transitioned;
        private Context previous;
        private State currentState;

        private BaseContextBuilder() {
        }

        public BaseContextBuilder stateMachineId(String stateMachineId) {
            this.stateMachineId = stateMachineId;
            return this;
        }

        public BaseContextBuilder resources(Set<Resource> resources) {
            this.resources = resources;
            return this;
        }

        public BaseContextBuilder e(Exception e) {
            this.e = e;
            return this;
        }

        public BaseContextBuilder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public BaseContextBuilder transitioned(LocalDateTime transitioned) {
            this.transitioned = transitioned;
            return this;
        }

        public BaseContextBuilder previous(Context previous) {
            this.previous = previous;
            return this;
        }

        public BaseContextBuilder currentState(State currentState) {
            this.currentState = currentState;
            return this;
        }
    }
}
