package pure.fsm.core.context;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;

import java.util.Optional;

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
}
