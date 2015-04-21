package pure.fsm.core.transition;

import pure.fsm.core.Transition;

public class InitialTransition {

    public static Transition initialTransition(Transition transition) {
        Transition curr = transition;
        while (curr.previous().isPresent()) {
            curr = curr.previous().get();
        }
        return curr;
    }
}
