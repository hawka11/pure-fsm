package pure.fsm.core.fixture;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestInitialContext {

    public final String data;

    @JsonCreator
    public TestInitialContext(
            @JsonProperty("data") String data
    ) {
        this.data = data;
    }
}
