package pure.fsm.example.kotlin.domain

import pure.fsm.core.EventTicker
import pure.fsm.core.Transition
import pure.fsm.core.TransitionRepository
import java.util.concurrent.TimeUnit

class TimeoutTicker(val repository: TransitionRepository,
                    val scheduleFrequency: Long,
                    val timeUnit: TimeUnit,
                    val f: (Transition) -> Transition) {


    private val ticker = EventTicker.defaultTicker(repository, scheduleFrequency, timeUnit, { t -> hasTimedout(t) }, { t -> f(t) })

    public fun start() = ticker.start()
    public fun end() = ticker.end()
    public fun tick() = ticker.tick()
}