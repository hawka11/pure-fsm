package pure.fsm.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import pure.fsm.core.event.Event;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;
import pure.fsm.core.trait.CanUnlockContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static pure.fsm.core.StateFactoryRegistration.getStateFactory;
import static pure.fsm.core.trait.InitialContext.createInitialContext;

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
    private final String stateFactoryClass;

    @JsonSerialize
    private List<Context> contexts;

    private transient StateFactory stateFactory;

    @JsonCreator
    private Transition(
            @JsonProperty("transitioned") LocalDateTime transitioned,
            @JsonProperty("state") String state,
            @JsonProperty("event") String event,
            @JsonProperty("previous") Transition previous,
            @JsonProperty("stateFactoryClass") String stateFactoryClass,
            @JsonProperty("contexts") List<Context> contexts) {
        this.transitioned = transitioned;
        this.state = state;
        this.event = event;
        this.previous = previous;
        this.stateFactoryClass = stateFactoryClass;
        this.contexts = contexts;
    }

    public static Transition initialTransition(String stateMachineId,
                                               State initialState,
                                               Class<? extends StateFactory> stateFactory,
                                               List<Context> initialContexts) {

        final List<Context> allContexts = newArrayList(createInitialContext(stateMachineId));
        allContexts.addAll(initialContexts);

        return new Transition(LocalDateTime.now(), initialState.getClass().getName(),
                "", null, stateFactory.getName(), allContexts);
    }

    @JsonIgnore
    public synchronized StateFactory stateFactory() {
        if (stateFactory == null) {
            initStateFactory();
        }
        return stateFactory;
    }

    private void initStateFactory() {
        final Optional<StateFactory> optional = getStateFactory(stateFactoryClass);

        Preconditions.checkArgument(optional.isPresent(),
                format("State factory class [%s] has not been registered via StateFactoryRegistration", stateFactoryClass));

        stateFactory = optional.get();
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

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public State getState() {
        try {
            final Class<? extends State> stateClass = (Class<? extends State>) Class.forName(state);
            return stateFactory().getStateByClass(stateClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(format("Could not find state of class [%s]", state));
        }
    }

    public Transition appendContext(CanUnlockContext trait) {
        contexts.add(trait);
        return this;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    public Transition transitionTo(State state, Event event) {
        return transitionTo(state, event, newArrayList());
    }

    public Transition transitionTo(State state, Event event, List<Context> contexts) {
        return new Transition(LocalDateTime.now(), state.getClass().getName(), event.getClass().getName(),
                this, stateFactoryClass, contexts);
    }

    public Transition transitionTo(Class<? extends State> state, Event event) {
        return transitionTo(state, event, newArrayList());
    }

    public Transition transitionTo(Class<? extends State> state, Event event, List<Context> contexts) {
        final State transitionState = stateFactory().getStateByClass(state);
        return transitionTo(transitionState, event, contexts);
    }

    @SuppressWarnings("unchecked")
    public <T extends Context> List<T> getContextsOfType(Class<T> contextClass) {
        final List<T> traits = (List<T>) contexts.stream()
                .filter(t -> contextClass.isAssignableFrom(t.getClass()))
                .collect(toList());
        return ImmutableList.copyOf(traits);
    }
}
