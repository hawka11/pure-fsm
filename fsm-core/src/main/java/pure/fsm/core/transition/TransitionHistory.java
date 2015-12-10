package pure.fsm.core.transition;

import com.google.common.base.Preconditions;
import pure.fsm.core.Transition;

import java.util.List;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Returns list of transitions (specified and historic) that satisfies
 * a certain predicate.
 * <p>
 * These are helper functions to get a list of the transition history instead
 * of navigating the 'previous' graph yourself.
 */
public class TransitionHistory {

    public static List<Transition> satisfies(Transition t, Predicate<Transition> p) {
        Preconditions.checkNotNull(t);

        final List<Transition> satisfied = newArrayList();

        Transition curr = t;

        if (p.test(curr)) satisfied.add(curr);

        while (curr.previous().isPresent()) {
            curr = curr.previous().get();

            if (p.test(curr)) satisfied.add(curr);
        }

        return satisfied;
    }

    public static List<Transition> skippingEvent(Transition t, Object event) {
        return satisfies(t, it -> !it.getEvent().equals(event.getClass().getName()));
    }

    public static List<Transition> skippingTimeoutTickTransitions(Transition t, Object timeoutTickEvent) {
        return skippingEvent(t, timeoutTickEvent);
    }
}
