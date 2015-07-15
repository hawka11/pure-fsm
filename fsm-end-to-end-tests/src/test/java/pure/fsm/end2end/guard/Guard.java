package pure.fsm.end2end.guard;

import pure.fsm.core.Context;

public interface Guard {
    boolean isSatisfied(Context context);
}
