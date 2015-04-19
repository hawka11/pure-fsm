package pure.fsm.core.event;

import pure.fsm.core.Transition;

public interface EventVisitor {

    Transition visit(Transition transition, TimeoutTickEvent timeoutTickEvent);
}
