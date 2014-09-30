package simple.fsm.core.event;

import simple.fsm.core.Context;

public abstract class BaseEvent<T extends EventVisitor> implements Event<T> {

    protected final Context context;

    protected BaseEvent(Context context) {
        this.context = context;
    }
}
