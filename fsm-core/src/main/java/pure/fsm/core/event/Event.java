package pure.fsm.core.event;

import pure.fsm.core.Context;
import pure.fsm.core.state.State;

public interface Event<C extends Context, T extends EventVisitor> {

    State accept(C context, T visitor);
}
