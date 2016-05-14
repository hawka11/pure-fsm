package pure.fsm.example.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import pure.fsm.core.WithinLock.tryWithLock
import pure.fsm.example.kotlin.domain.TelcoEvent.RechargeConfirmEvent
import pure.fsm.example.kotlin.domain.TelcoEvent.RechargeEvent
import pure.fsm.example.kotlin.domain.TelcoState.*
import pure.fsm.example.kotlin.domain.TelcoStateMachine
import pure.fsm.repository.inmemory.InMemoryTransitionRepository
import java.math.BigDecimal

class SuccessfulTest {

    private val repository = InMemoryTransitionRepository()
    private val stateMachine = TelcoStateMachine()

    private fun latest(id: String) = repository.get(id)

    @Test
    fun runTest() {
        val id = repository.create(InitialState, listOf())

        tryWithLock(id, repository, { t -> stateMachine.handleEvent(t, RechargeEvent(BigDecimal("20.00"), listOf("555", "666"))) })
        assertThat(latest(id).state).isEqualTo(RechargeRequestedState)

        Thread.sleep(500)

        tryWithLock(id, repository, { t -> stateMachine.handleEvent(t, RechargeConfirmEvent(listOf("555", "666"))) })
        assertThat(latest(id).state).isEqualTo(RechargeConfirmedFinalState)
    }
}