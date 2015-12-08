package pure.fsm.core;

public interface FinalState {

    class ErrorFinalState implements FinalState {
    }

    ErrorFinalState ERROR_FINAL_STATE = new ErrorFinalState();
}

