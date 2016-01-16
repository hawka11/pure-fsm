package pure.fsm.example.kotlin.domain

import pure.fsm.core.FinalState
import pure.fsm.core.StateMachine
import pure.fsm.core.Transition
import pure.fsm.example.kotlin.domain.TelcoEvent.*
import pure.fsm.example.kotlin.domain.TelcoState.*

class TelcoStateMachine : StateMachine<TelcoEvent>() {

    init {

        onReceive(InitialState, { last, event ->
            when (event) {
                is RechargeEvent -> go(RechargeRequestedState, event, last.context)
                is TimeoutTick -> checkTimeout(last)
                else -> defaultHandle(last, event)
            }
        })

        onReceive(RechargeRequestedState, { last, event ->
            when (event) {
                is RechargeConfirmEvent -> go(RechargeConfirmedFinalState, event, last.context)
                is TimeoutTick -> checkTimeout(last)
                else -> defaultHandle(last, event)
            }
        })

        onTransition(TelcoState::class.java, FinalState::class.java, { t, event ->
            println("hey, we got to final state")
        })
    }

    private fun checkTimeout(last: Transition) = if (hasTimedout(last)) go(TimeoutFinalState, TimeoutTick, last.context) else stay(last.state, TimeoutTick, last.context)

    private fun defaultHandle(last: Transition, event: TelcoEvent) = go(ErrorFinalState, event, last.context)
}