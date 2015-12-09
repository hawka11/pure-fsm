package pure.fsm.core;

public interface FinalState {

    class TimeoutErrorFinalState implements FinalState {
    }

    class ErrorFinalState implements FinalState {
    }

    class SuccessFinalState implements FinalState {
    }

    class UserCancelledFinalState implements FinalState {
    }

    TimeoutErrorFinalState TIMEOUT_ERROR_FINAL_STATE = new TimeoutErrorFinalState();
    ErrorFinalState ERROR_FINAL_STATE = new ErrorFinalState();
    SuccessFinalState SUCCESS_FINAL_STATE = new SuccessFinalState();
    UserCancelledFinalState USER_CANCELLED_FINAL_STATE = new UserCancelledFinalState();
}

