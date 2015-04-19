package pure.fsm.telco.guard;

import pure.fsm.core.Transition;

public interface Guard {
    boolean isSatisfied(Transition transition);
}
