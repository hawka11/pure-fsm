package pure.fsm.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pure.fsm.core.event.Event;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static pure.fsm.core.Context.initialContext;

public class Transition {

    @JsonSerialize
    private final LocalDateTime transitioned;

    @JsonSerialize
    private final String state;

    @JsonSerialize
    private final String event;

    @JsonSerialize
    private final Transition previous;

    @JsonSerialize
    private Context context;

    @JsonCreator
    private Transition(
            @JsonProperty("transitioned") LocalDateTime transitioned,
            @JsonProperty("state") String state,
            @JsonProperty("event") String event,
            @JsonProperty("previous") Transition previous,
            @JsonProperty("contexts") Context context) {
        this.transitioned = transitioned;
        this.state = state;
        this.event = event;
        this.previous = previous;
        this.context = context;
    }

    public static Transition initialTransition(String stateMachineId,
                                               State initialState,
                                               Class<? extends StateFactory> stateFactory,
                                               List<Object> initialContexts) {

        final Context context = initialContext(stateMachineId, stateFactory, initialContexts);

        return new Transition(LocalDateTime.now(),
                initialState.getClass().getName(),
                "", null, context);
    }

    @JsonIgnore
    public Optional<Transition> previous() {
        return ofNullable(previous);
    }

    public String getEvent() {
        return event;
    }

    public LocalDateTime getTransitioned() {
        return transitioned;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    public static Transition To(State state, Event event, Context context) {
        return new Transition(LocalDateTime.now(),
                state.getClass().getName(),
                event.getClass().getName(),
                null, context);
    }

    public static Transition To(Class<? extends State> state, Event event, Context context) {
        final State stateByClass = context.stateFactory().getStateByClass(state);
        return To(stateByClass, event, context);
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public State getState() {
        try {
            final Class<? extends State> stateClass = (Class<? extends State>) Class.forName(state);
            return context.stateFactory().getStateByClass(stateClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(format("Could not find state of class [%s]", state));
        }
    }

    public Transition setNextTransition(Transition nextTransition) {
        return new Transition(nextTransition.transitioned, nextTransition.state,
                nextTransition.event, this, nextTransition.context);
    }

}
