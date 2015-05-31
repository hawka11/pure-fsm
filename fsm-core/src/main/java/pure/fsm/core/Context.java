package pure.fsm.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import pure.fsm.core.context.CanUnlock;
import pure.fsm.core.context.InitialContext;
import pure.fsm.core.state.StateFactory;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static pure.fsm.core.StateFactoryRegistration.getStateFactory;
import static pure.fsm.core.context.InitialContext.createInitialContext;

public class Context {

    @JsonSerialize
    private final String stateFactoryClass;
    @JsonSerialize
    private final List<Object> contextState;

    private transient StateFactory stateFactory;

    @JsonCreator
    private Context(
            @JsonProperty("stateFactoryClass") String stateFactoryClass,
            @JsonProperty("contextState") List<Object> contextState) {
        this.stateFactoryClass = stateFactoryClass;
        this.contextState = contextState;
    }

    public static Context initialContext(String stateMachineId,
                                         Class<? extends StateFactory> stateFactory,
                                         List<Object> initialContexts) {

        final List<Object> allContexts = newArrayList(createInitialContext(stateMachineId));
        allContexts.addAll(initialContexts);

        return new Context(stateFactory.getName(), allContexts);
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

    @SuppressWarnings("unchecked")
    public <T> List<T> getContextsOfType(Class<T> contextClass) {
        final List<T> contexts = (List<T>) this.contextState.stream()
                .filter(t -> contextClass.isAssignableFrom(t.getClass()))
                .collect(toList());
        return ImmutableList.copyOf(contexts);
    }

    public <T> Optional<T> mostRecentOf(Class<T> klass) {
        return reverse(getContextsOfType(klass)).stream().findFirst();
    }

    public String stateMachineId() {
        return InitialContext.initialContext(this).stateMachineId;
    }

    public Context addCanUnlock(CanUnlock canUnlock) {
        contextState.add(canUnlock);
        return this;
    }

    public Context appendState(Object state) {
        final List<Object> newData = newArrayList();
        newData.addAll(this.contextState);
        newData.add(state);
        return new Context(stateFactoryClass, newData);
    }
}
