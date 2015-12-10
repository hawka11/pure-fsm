package pure.fsm.core.repository;

import pure.fsm.core.FinalState;
import pure.fsm.core.TransitionRepository;
import pure.fsm.core.Transition;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static pure.fsm.core.context.InitialContext.initialContext;

public class InProgress {

    public static Set<String> inProgressIds(TransitionRepository repository) {
        return inProgressTransitions(repository).stream()
                .map(last -> initialContext(last.getContext()).stateMachineId)
                .collect(toSet());
    }

    public static Set<Transition> inProgressTransitions(TransitionRepository repository) {
        return repository.getIds().stream()
                .map(repository::get)
                .filter(last -> !FinalState.class.isAssignableFrom(last.getState().getClass()))
                .collect(toSet());
    }
}
