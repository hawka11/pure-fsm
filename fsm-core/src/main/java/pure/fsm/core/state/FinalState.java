package pure.fsm.core.state;

public interface FinalState {

    enum DefaultFinalStates {
        //success
        SUCCESS_FINAL_STATE,
        USER_CANCELLED_SUCCESS_FINAL_STATE,

        //error
        ERROR_FINAL_STATE,
        TIMED_OUT_FINAL_STATE
    }
}

