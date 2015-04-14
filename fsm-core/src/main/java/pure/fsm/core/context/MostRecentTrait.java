package pure.fsm.core.context;

import pure.fsm.core.Context;
import pure.fsm.core.state.State;
import pure.fsm.core.trait.Trait;
import pure.fsm.core.trait.TransitionedTrait;

import java.util.Optional;

import static com.google.common.collect.Lists.reverse;

public class MostRecentTrait {

    public static <T extends Trait> Optional<T> mostRecentOf(Context context, Class<T> klass) {
        return reverse(context.getTraitsOf(klass)).stream().findFirst();
    }

    public static TransitionedTrait mostRecentTransition(Context context) {
        return mostRecentOf(context, TransitionedTrait.class).get();
    }

    public static State currentState(Context context) {
        return mostRecentTransition(context).state;
    }
}
