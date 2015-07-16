package pure.fsm.example.inmemory.guard;

import pure.fsm.core.Context;

public interface Guard {
    boolean isSatisfied(Context context);
}
