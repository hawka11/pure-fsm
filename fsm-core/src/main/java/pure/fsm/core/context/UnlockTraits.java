package pure.fsm.core.context;

import pure.fsm.core.Context;
import pure.fsm.core.trait.CanUnlockTrait;

import java.util.List;

public class UnlockTraits {

    public static void unlockTraits(Context context) {
        final List<CanUnlockTrait> unlockable = context.getTraitsOf(CanUnlockTrait.class);
        unlockable.stream().forEach(CanUnlockTrait::unlock);
    }
}
