package pure.fsm.telco.event;

import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

import java.math.BigDecimal;
import java.util.Set;

public class RequestRechargeEvent implements Event<TelcoEventVisitor> {

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
    public Transition accept(Transition transition, TelcoEventVisitor visitor) {
        return visitor.visit(transition, this);
    }

    public Set<String> getPinsToLock() {
        return pinsToLock;
    }
}
