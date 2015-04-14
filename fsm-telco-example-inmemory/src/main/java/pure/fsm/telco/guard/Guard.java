package pure.fsm.telco.guard;

import pure.fsm.core.Context;

public interface Guard {
    boolean isSatisfied(Context context);
}
