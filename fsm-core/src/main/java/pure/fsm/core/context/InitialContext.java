package pure.fsm.core.context;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;

public class InitialContext implements Context {

    public final String stateMachineId;

    @JsonCreator
    private InitialContext(@JsonProperty("stateMachineId") String stateMachineId) {
        this.stateMachineId = stateMachineId;
    }

    public static InitialContext createInitialContext(String stateMachineId) {
        return new InitialContext(stateMachineId);
    }

    public static InitialContext initialContext(Transition transition) {
        Transition curr = transition;
        while (curr.previous().isPresent()) {
            curr = curr.previous().get();
        }
        return curr.getContextsOfType(InitialContext.class).get(0);
    }
}
