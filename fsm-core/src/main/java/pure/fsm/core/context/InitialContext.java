package pure.fsm.core.context;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.Context;

public class InitialContext {

    public final String stateMachineId;

    @JsonCreator
    private InitialContext(@JsonProperty("stateMachineId") String stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public static InitialContext createInitialContext(String stateMachineId) {
        return new InitialContext(stateMachineId);
    }

    public static InitialContext initialContext(Context context) {
        return context.getContextsOfType(InitialContext.class).get(0);
    }
}
