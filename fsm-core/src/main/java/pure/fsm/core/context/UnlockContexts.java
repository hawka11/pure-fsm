package pure.fsm.core.context;

import pure.fsm.core.Transition;
import pure.fsm.core.trait.CanUnlockContext;

import java.util.List;

public class UnlockContexts {

    public static void unlockContexts(Transition transition) {
        final List<CanUnlockContext> unlockable = transition.getContextsOfType(CanUnlockContext.class);

        unlockable.stream().forEach(CanUnlockContext::unlock);

        if (transition.previous().isPresent()) {
            unlockContexts(transition.previous().get());
        }
    }
}
