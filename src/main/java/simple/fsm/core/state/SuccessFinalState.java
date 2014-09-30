package simple.fsm.core.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;

public class SuccessFinalState implements FinalState {

    private final String code;
    private final String message;

    public SuccessFinalState(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static SuccessFinalState userCanceled() {
        return new SuccessFinalState("USER_CANCELED", "User cancelled");
    }

    @Override
    public State handle(Context context, Event event) {
        return null;
    }

    @Override
    public void onExit(Context context, Event event) {
    }

    @Override
    public void onEntry(Context context, Event event, State prevState) {
    }
}
