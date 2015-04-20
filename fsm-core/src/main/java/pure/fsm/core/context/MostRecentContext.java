package pure.fsm.core.context;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;

public class MostRecentContext {

    public static <T extends Context> Optional<T> mostRecentOf(Transition transition, Class<T> klass) {
        final Optional<T> maybeFound = reverse(transition.getContextsOfType(klass)).stream().findFirst();
        if (maybeFound.isPresent()) {
            return maybeFound;
        } else if (transition.previous().isPresent()) {
            return mostRecentOf(transition.previous().get(), klass);
        }
        return Optional.empty();
    }

    public static <T extends Context> List<T> findAllOfType(Transition transition, Class<T> klass) {
        final List<T> ofType = newArrayList(transition.getContextsOfType(klass));

        if (transition.previous().isPresent()) {
            ofType.addAll(findAllOfType(transition.previous().get(), klass));
        }

        return ofType;
    }
}
