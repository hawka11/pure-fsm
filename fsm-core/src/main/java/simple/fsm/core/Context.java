package simple.fsm.core;

import java.time.LocalDateTime;
import java.util.Set;

public interface Context {

    LocalDateTime getTransitioned();

    void addResource(Resource resource);

    Set<Resource> getResources();

    void unlockResources();

    Exception getException();

    void setException(Exception e);

    String getMessage();

    void setMessage(String msg);

    Context transition();
}
