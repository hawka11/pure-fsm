package pure.fsm.core.state;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.TimeoutTickEvent;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public interface State {

    Transition handle(Context context, Event event);

    Transition handle(Transition prevTransition, TimeoutTickEvent event);

    boolean isTimeout(Transition prevTransition);

    void onExit(Context context, Event event);

    void onEntry(Context context, Event event, State prevState);
}
