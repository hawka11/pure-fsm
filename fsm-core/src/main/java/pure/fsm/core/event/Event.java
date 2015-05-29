package pure.fsm.core.event;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;

public interface Event<T extends EventVisitor> {

    Transition accept(Context context, T visitor);
}
