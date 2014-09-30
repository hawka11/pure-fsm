package simple.fsm.optus.event;

import simple.fsm.core.event.Event;
import simple.fsm.core.state.State;
import simple.fsm.optus.OptusRechargeContext;

import java.math.BigDecimal;

public class RequestRechargeEvent implements Event<OptusRechargeContext, OptusEventVisitor> {

    private final BigDecimal amount;

    public RequestRechargeEvent(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public State accept(OptusRechargeContext context, OptusEventVisitor visitor) {
        return visitor.visit(context, this);
    }
}
