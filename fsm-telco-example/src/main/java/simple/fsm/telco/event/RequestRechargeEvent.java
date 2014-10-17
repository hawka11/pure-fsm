package simple.fsm.telco.event;

import simple.fsm.core.event.Event;
import simple.fsm.core.state.State;
import simple.fsm.telco.TelcoRechargeContext;

import java.math.BigDecimal;

public class RequestRechargeEvent implements Event<TelcoRechargeContext, TelcoEventVisitor> {

    private final BigDecimal amount;

    public RequestRechargeEvent(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public State accept(TelcoRechargeContext context, TelcoEventVisitor visitor) {
        return visitor.visit(context, this);
    }
}
