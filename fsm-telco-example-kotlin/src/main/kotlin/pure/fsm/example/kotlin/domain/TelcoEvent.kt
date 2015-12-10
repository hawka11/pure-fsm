package pure.fsm.example.kotlin.domain

import java.math.BigDecimal

public sealed class TelcoEvent {

    class RechargeEvent(val amount: BigDecimal, val pins: List<String>) : TelcoEvent()

    class RechargeConfirmEvent(val pins: List<String>) : TelcoEvent()

    object TimeoutTick : TelcoEvent()
}