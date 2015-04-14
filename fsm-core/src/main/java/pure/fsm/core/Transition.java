package pure.fsm.core;

import pure.fsm.core.state.State;

public class Transition<T extends State> {

    public final T state;
    public final Context context;

    private Transition(T state, Context context) {
        this.state = state;
        this.context = context;
    }

    public static <S extends State> Transition<S> transition(S state, Context context) {
        return new Transition<>(state, context);
    }

}
