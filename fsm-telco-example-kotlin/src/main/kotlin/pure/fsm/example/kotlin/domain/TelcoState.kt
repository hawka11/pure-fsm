package pure.fsm.example.kotlin.domain

import pure.fsm.core.FinalState
import pure.fsm.core.Transition
import java.time.LocalDateTime

public val TIMEOUT_SECS = 1L

public sealed class TelcoState {

    //all states timeout
    fun isTimeout(t: Transition) = LocalDateTime.now().isAfter(t.transitioned.plusSeconds(TIMEOUT_SECS))

    //states

    object InitialState : TelcoState()

    object RechargeRequestedState : TelcoState()

    object RechargeConfirmedFinalState : TelcoState(), FinalState

    object TimeoutFinalState : TelcoState(), FinalState

    object ErrorFinalState : TelcoState(), FinalState
}

val hasTimedout = { t: Transition ->
    val state = t.state
    when (state) {
        is TelcoState -> state.isTimeout(t)
        else -> true
    }
}