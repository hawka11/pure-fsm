package simple.fsm.core.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;

import java.time.LocalDateTime;

public interface State {

    LocalDateTime getCreated();

    State handle(Context context, Event event);

    void onExit(Context context, Event event);

    void onEntry(Context context, Event event, State prevState);
}
