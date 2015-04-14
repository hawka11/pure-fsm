package pure.fsm.core;

import pure.fsm.core.state.State;

public class Transition<T extends State> {

    public final T state;
    public final Context context;

    public Transition(T state, Context context) {
        this.state = state;
        this.context = context;
    }
}
