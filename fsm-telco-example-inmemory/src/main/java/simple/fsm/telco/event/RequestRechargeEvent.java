package simple.fsm.telco.event;

import simple.fsm.core.event.Event;
import simple.fsm.core.state.State;
import simple.fsm.telco.TelcoRechargeContext;

import java.math.BigDecimal;
import java.util.Set;

public class RequestRechargeEvent implements Event<TelcoRechargeContext, TelcoEventVisitor> {

    private final BigDecimal amount;
    private final Set<String> pinsToLock;

    public RequestRechargeEvent(BigDecimal amount, Set<String> pinsToLock) {
        this.amount = amount;
        this.pinsToLock = pinsToLock;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public State accept(TelcoRechargeContext context, TelcoEventVisitor visitor) {
        return visitor.visit(context, this);
    }

    public Set<String> getPinsToLock() {
        return pinsToLock;
    }
}
