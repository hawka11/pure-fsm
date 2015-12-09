package pure.fsm.java.test.fixture.guard;

import pure.fsm.core.Context;

public interface Guard {
    boolean isSatisfied(Context context);
}
