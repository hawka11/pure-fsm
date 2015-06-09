package pure.fsm.core.cleanup;

import pure.fsm.core.Transition;

public interface OnCleanupListener {
    void onCleanup(Transition transition);
}
