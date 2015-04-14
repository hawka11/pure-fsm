package pure.fsm.core.trait;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageTrait implements Trait {

    public final String message;

    @JsonCreator
    private MessageTrait(@JsonProperty("message") String message) {
        this.message = message;
    }

    public static MessageTrait withMessage(String message) {
        return new MessageTrait(message);
    }
}
