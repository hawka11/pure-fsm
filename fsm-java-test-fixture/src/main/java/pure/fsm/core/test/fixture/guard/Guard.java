package pure.fsm.core.test.fixture.guard;

import pure.fsm.core.Context;

public interface Guard {
    boolean isSatisfied(Context context);
}
