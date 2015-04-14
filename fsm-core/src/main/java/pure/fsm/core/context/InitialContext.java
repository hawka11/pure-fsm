package pure.fsm.core.context;

import pure.fsm.core.Context;

public class InitialContext {

    public static Context initialContext(Context context) {
        Context curr = context;
        while (curr.previous().isPresent()) {
            curr = curr.previous().get();
        }
        return curr;
    }
}
