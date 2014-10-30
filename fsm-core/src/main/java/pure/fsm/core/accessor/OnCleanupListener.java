package pure.fsm.core.accessor;

import pure.fsm.core.Context;

public interface OnCleanupListener {
    void onCleanup(Context context);
}
