package pure.fsm.example.kotlin.domain

import pure.fsm.core.FinalState

public sealed class TelcoState {

    object InitialState : TelcoState()

    object RechargeRequestedState : TelcoState()

    object RechargeConfirmedFinalState : TelcoState(), FinalState

    object TimeoutFinalState : TelcoState(), FinalState

    object ErrorFinalState : TelcoState(), FinalState
}