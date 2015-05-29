package pure.fsm.core.context;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExceptionContext {

    public final Exception e;

    @JsonCreator
    private ExceptionContext(@JsonProperty("e") Exception e) {
        this.e = e;
    }

    public static ExceptionContext withException(Exception e) {
        return new ExceptionContext(e);
    }
}
