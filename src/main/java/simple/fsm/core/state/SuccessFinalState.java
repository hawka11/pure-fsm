package simple.fsm.core.state;

import simple.fsm.core.Context;
import simple.fsm.core.event.Event;

import java.time.LocalDateTime;

public class SuccessFinalState implements FinalState {

    private final String code;
    private final String message;
    private final LocalDateTime createdDateTime;

    public SuccessFinalState(String code, String message) {
        this.code = code;
        this.message = message;
        this.createdDateTime = LocalDateTime.now();
    }

    public static SuccessFinalState userCanceled() {
        return new SuccessFinalState("USER_CANCELED", "User cancelled");
    }

    @Override
    public LocalDateTime getCreated() {
        return createdDateTime;
    }

    @Override
    public State handle(Context context, Event event) {

        throw new IllegalStateException("In SuccessFinalState, cannot process any more events");
    }

    @Override
    public void onExit(Context context, Event event) {
    }

    @Override
    public void onEntry(Context context, Event event, State prevState) {
    }
}
