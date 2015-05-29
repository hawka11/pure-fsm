package pure.fsm.core.context;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ContextMessage {

    public final String message;

    @JsonCreator
    private ContextMessage(@JsonProperty("message") String message) {
        this.message = message;
    }

    public static ContextMessage withMessage(String message) {
        return new ContextMessage(message);
    }
}
