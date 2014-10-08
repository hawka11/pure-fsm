package simple.fsm.core.event;

import simple.fsm.core.Context;
import simple.fsm.core.state.State;

public interface EventVisitor {

    State visit(Context context, TimeoutTickEvent timeoutTickEvent);
}
