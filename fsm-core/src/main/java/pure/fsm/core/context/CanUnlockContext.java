package pure.fsm.core.context;

import pure.fsm.core.Context;

public interface CanUnlockContext extends Context {
    void unlock();
}
