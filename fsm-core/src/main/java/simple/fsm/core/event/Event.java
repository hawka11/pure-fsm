package simple.fsm.core.event;

import simple.fsm.core.Context;
import simple.fsm.core.state.State;

public interface Event<C extends Context, T extends EventVisitor> {

    State accept(C context, T visitor);
}
