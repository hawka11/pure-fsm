package pure.fsm.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import pure.fsm.core.event.Event;
import pure.fsm.core.state.State;
import pure.fsm.core.trait.CanUnlockTrait;
import pure.fsm.core.trait.Trait;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static pure.fsm.core.trait.TransitionedTrait.transitioned;

public class Context {

    public final String stateMachineId;

    @JsonSerialize
    private final Context previous;

    @JsonSerialize
    private List<Trait> traits;

    @JsonCreator
    private Context(
            @JsonProperty("stateMachineId") String stateMachineId,
            @JsonProperty("previous") Context previous,
            @JsonProperty("traits") List<Trait> traits) {
        this.stateMachineId = stateMachineId;
        this.previous = previous;
        this.traits = traits;
    }

    public static Context initialContext(String stateMachineId, State initialState, List<? extends Trait> initialTraits) {
        final Context context = new Context(stateMachineId, null, newArrayList())
                .addTrait(transitioned(LocalDateTime.now(), initialState, Optional.empty()));

        return initialTraits.stream().reduce(context, Context::addTrait, (c, t) -> c);
    }

    @JsonIgnore
    public Optional<Context> previous() {
        return ofNullable(previous);
    }

    public Context addTrait(Trait trait) {
        final List<Trait> newTraits = newArrayList();
        newTraits.addAll(traits);
        newTraits.add(trait);
        return new Context(stateMachineId, previous, newTraits);
    }

    public Context addTrait(CanUnlockTrait trait) {
        traits.add(trait);
        return this;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    private Context withPrevious(Context previousContext) {
        return new Context(stateMachineId, previousContext, traits);
    }

    public Transition transition(State state, Event event) {
        final Context transitioned = withPrevious(this)
                .addTrait(transitioned(LocalDateTime.now(), state, Optional.ofNullable(event)));

        return Transition.transition(state, transitioned);
    }

    @SuppressWarnings("unchecked")
    public <T extends Trait> List<T> getTraitsOf(Class<T> klass) {
        final List<T> traits = (List<T>) this.traits.stream()
                .filter(t -> klass.isAssignableFrom(t.getClass()))
                .collect(toList());
        return ImmutableList.copyOf(traits);
    }

    //StateFactory stateFactory();
}
