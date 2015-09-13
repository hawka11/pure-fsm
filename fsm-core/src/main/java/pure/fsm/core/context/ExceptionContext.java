package pure.fsm.core.context;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionContext {

    public final String message;
    public final String stackTrace;

    @JsonCreator
    private ExceptionContext(@JsonProperty("message") String message,
                             @JsonProperty("stackTrace") String stackTrace) {
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public static ExceptionContext withException(Exception e) {
        final String message = e.getMessage();
        final String stackTrace = ExceptionUtils.getStackTrace(e);
        return new ExceptionContext(message, stackTrace);
    }
}
