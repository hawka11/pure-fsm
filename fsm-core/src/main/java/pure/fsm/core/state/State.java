package pure.fsm.core.state;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public interface State {

    Transition handle(Transition prevTransition, Event event);

    boolean isTimeout(Transition prevTransition);

    void onExit(Transition prevTransition, Event event);

    void onEntry(Transition prevTransition, Event event, State prevState);
}
