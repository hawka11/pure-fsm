package simple.fsm.core;

import java.time.LocalDateTime;
import java.util.Set;

public abstract class BaseContext implements Context {

    private final Set<Resource> resources;
    private Exception e;
    private String msg;
    private final LocalDateTime transitioned;

    protected BaseContext(Set<Resource> resources, Exception e, String msg, LocalDateTime transitioned) {
        this.resources = resources;
        this.e = e;
        this.msg = msg;
        this.transitioned = transitioned;
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

    protected Set<Resource> getResources() {
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
}
