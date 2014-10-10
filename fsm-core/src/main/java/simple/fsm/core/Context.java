package simple.fsm.core;

import java.time.LocalDateTime;

public interface Context {

    LocalDateTime getTransitioned();

    void addResource(Resource resource);

    void unlockResources();

    Exception getException();

    void setException(Exception e);

    String getMessage();

    void setMessage(String msg);

    Context transition();
}
