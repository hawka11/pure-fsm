package pure.fsm.example.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import pure.fsm.core.EventTicker
import pure.fsm.core.WithinLock.tryWithLock
import pure.fsm.example.kotlin.domain.TIMEOUT_SECS
import pure.fsm.example.kotlin.domain.TelcoEvent.*
import pure.fsm.example.kotlin.domain.TelcoEvent.RechargeEvent
import pure.fsm.example.kotlin.domain.TelcoState.*
import pure.fsm.example.kotlin.domain.TelcoStateMachine
import pure.fsm.repository.inmemory.InMemoryTransitionRepository
import java.math.BigDecimal
import java.util.concurrent.TimeUnit.SECONDS

class SuccessfulTest {

    private val repository = InMemoryTransitionRepository()
    private val stateMachine = TelcoStateMachine()
    private val timeoutTicker = EventTicker(repository, 1, SECONDS, { t -> stateMachine.handleEvent(t, TimeoutTick) })

    private fun latest(id: String) = repository.get(id)

    @Test
    fun runTest() {
        val id = repository.create(InitialState, listOf())

        val next = tryWithLock(id, repository, { t -> stateMachine.handleEvent(t, RechargeEvent(BigDecimal("20.00"), listOf("555", "666"))) })
        assertThat(next.state).isEqualTo(RechargeRequestedState)

        //allow for timeout
        Thread.sleep(SECONDS.toMillis(TIMEOUT_SECS) + 500)

        timeoutTicker.tick()
        Thread.sleep(500)

        assertThat(latest(id).state).isEqualTo(TimeoutFinalState)
    }
}