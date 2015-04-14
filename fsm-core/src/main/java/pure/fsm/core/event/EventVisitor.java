package pure.fsm.core.event;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;

public interface EventVisitor {

    Transition visit(Context context, TimeoutTickEvent timeoutTickEvent);
}
