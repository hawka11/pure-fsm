package simple.fsm.state;

import simple.fsm.Context;
import simple.fsm.event.Event;

public interface State {
    State handle(Context context, Event event);
}
