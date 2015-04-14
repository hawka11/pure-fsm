package pure.fsm.core.trait;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExceptionTrait implements Trait {

    public final Exception e;

    @JsonCreator
    private ExceptionTrait(@JsonProperty("e") Exception e) {
        this.e = e;
    }

    public static ExceptionTrait withException(Exception e) {
        return new ExceptionTrait(e);
    }
}
