package pure.fsm.core.state;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pure.fsm.core.Context;
import pure.fsm.core.event.Event;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public interface State {

    StateFactory factory();

    State handle(Context context, Event event);

    void onExit(Context context, Event event);

    void onEntry(Context context, Event event, State prevState);
}
