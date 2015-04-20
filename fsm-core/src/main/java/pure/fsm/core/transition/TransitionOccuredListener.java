package pure.fsm.core.transition;

import pure.fsm.core.Transition;

public interface TransitionOccuredListener {

    void onTransition(Transition prevTransition, Transition newTransition);
}
