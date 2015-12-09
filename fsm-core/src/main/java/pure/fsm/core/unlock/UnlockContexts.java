package pure.fsm.core.unlock;

import pure.fsm.core.Context;

import java.util.List;

public class UnlockContexts {

    public static void unlockContexts(Context context) {
        final List<CanUnlock> unlockable = context.getContextsOfType(CanUnlock.class);

        unlockable.stream().forEach(CanUnlock::unlock);
    }
}
