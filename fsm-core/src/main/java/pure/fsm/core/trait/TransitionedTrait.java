package pure.fsm.core.trait;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.event.Event;
import pure.fsm.core.state.State;

import java.time.LocalDateTime;
import java.util.Optional;

public class TransitionedTrait implements Trait {
    public final LocalDateTime transitioned;
    public final State state;
    public final String event;

    @JsonCreator
    private TransitionedTrait(
            @JsonProperty("transitioned") LocalDateTime transitioned,
            @JsonProperty("state") State state,
            @JsonProperty("event") String event) {
        this.transitioned = transitioned;
        this.state = state;
        this.event = event;
    }

    public static TransitionedTrait transitioned(LocalDateTime transitioned, State state, Optional<Event> event) {
        final String eventString = event.isPresent() ? event.get().toString() : "<NONE>";
        return new TransitionedTrait(transitioned, state, eventString);
    }
}
