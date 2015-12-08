package pure.fsm.core.fixture;

import pure.fsm.core.FinalState;

public interface TestState {

    class InitialState implements TestState {
    }

    class RechargeRequestedState implements TestState {
    }

    class RechargeAcceptedFinalState implements TestState, FinalState {
    }

    InitialState INITIAL_STATE = new InitialState();
    RechargeRequestedState RECHARGE_REQUESTED_STATE = new RechargeRequestedState();
    RechargeAcceptedFinalState RECHARGE_ACCEPTED_FINAL_STATE = new RechargeAcceptedFinalState();
}
