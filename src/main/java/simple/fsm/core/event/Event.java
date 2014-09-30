package simple.fsm.core.event;

import simple.fsm.core.state.State;

public interface Event<T extends EventVisitor> {

    State accept(T visitor);
}
