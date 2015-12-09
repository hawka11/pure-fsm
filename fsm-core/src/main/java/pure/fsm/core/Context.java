package pure.fsm.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import pure.fsm.core.unlock.CanUnlock;
import pure.fsm.core.context.InitialContext;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;
import static java.util.stream.Collectors.toList;
import static pure.fsm.core.context.InitialContext.createInitialContext;

public class Context {

    @JsonSerialize
    private final List<Object> data;

    @JsonCreator
    private Context(@JsonProperty("data") List<Object> data) {
        this.data = data;
    }

    public static Context initialContext(String stateMachineId, List<Object> initialContexts) {
        final List<Object> allData = newArrayList(createInitialContext(stateMachineId));
        allData.addAll(initialContexts);

        return new Context(allData);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getContextsOfType(Class<T> contextClass) {
        final List<T> contexts = (List<T>) this.data.stream()
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
        data.add(canUnlock);
        return this;
    }

    public Context appendState(Object state) {
        final List<Object> newData = newArrayList();
        newData.addAll(this.data);
        newData.add(state);
        return new Context(newData);
    }
}