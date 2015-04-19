package pure.fsm.core.accessor;

import pure.fsm.core.Transition;

public interface OnCleanupListener {
    void onCleanup(Transition transition);
}
