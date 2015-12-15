package pure.fsm.example.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import pure.fsm.core.EventTicker
import pure.fsm.core.WithinLock.tryWithLock
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines
import pure.fsm.example.kotlin.domain.TIMEOUT_SECS
import pure.fsm.example.kotlin.domain.TelcoEvent.RechargeEvent
import pure.fsm.example.kotlin.domain.TelcoEvent.TimeoutTick
import pure.fsm.example.kotlin.domain.TelcoState.*
import pure.fsm.example.kotlin.domain.TelcoStateMachine
import pure.fsm.repository.inmemory.InMemoryTransitionRepository
import java.math.BigDecimal
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS

class TimeoutAndCleanupTest {

    private val KEEP_FINALIZED_SEC = 1L

    private val repository = InMemoryTransitionRepository()
    private val stateMachine = TelcoStateMachine()
    private val timeoutTicker = EventTicker(repository, 1, SECONDS, { t -> stateMachine.handleEvent(t, TimeoutTick) })
    private val cleaner = CleanUpFinalisedStateMachines(repository, listOf(), 1, TimeUnit.SECONDS, KEEP_FINALIZED_SEC, ChronoUnit.SECONDS)

    private fun latest(id: String) = repository.get(id)

    @Test
    fun runTest() {
        val id = repository.create(InitialState, listOf())

        tryWithLock(id, repository, { t -> stateMachine.handleEvent(t, RechargeEvent(BigDecimal("20.00"), listOf("555", "666"))) })
        assertThat(latest(id).state).isEqualTo(RechargeRequestedState)

        //allow for timeout
        Thread.sleep(SECONDS.toMillis(TIMEOUT_SECS) + 500)

        //send timeout ticker to timeout
        timeoutTicker.tick()
        assertThat(latest(id).state).isEqualTo(TimeoutFinalState)

        //allow for KEEP_FINALIZED_SEC
        Thread.sleep(SECONDS.toMillis(KEEP_FINALIZED_SEC) + 500)

        //cleanup state
        cleaner.checkForFinalizedStateMachinesAndCleanupIfRequired()
        assertThat(latest(id)).isNull()
    }
}