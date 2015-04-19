package pure.fsm.core.context;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.Context;

public class MessageContext implements Context {

    public final String message;

    @JsonCreator
    private MessageContext(@JsonProperty("message") String message) {
        this.message = message;
    }

    public static MessageContext withMessage(String message) {
        return new MessageContext(message);
    }
}
