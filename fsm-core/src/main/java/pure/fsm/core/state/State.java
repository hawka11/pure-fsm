package pure.fsm.core.state;

import pure.fsm.core.Context;
import pure.fsm.core.event.Event;

public interface State {

    StateFactory factory();

    State handle(Context context, Event event);

    void onExit(Context context, Event event);

    void onEntry(Context context, Event event, State prevState);
}
