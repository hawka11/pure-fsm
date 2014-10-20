package pure.fsm.core.event;

import pure.fsm.core.state.State;
import pure.fsm.core.Context;

public interface EventVisitor {

    State visit(Context context, TimeoutTickEvent timeoutTickEvent);
}
