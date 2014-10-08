package simple.fsm.core;

public interface Context {

    void addResource(Resource resource);

    void unlockResources();
}
