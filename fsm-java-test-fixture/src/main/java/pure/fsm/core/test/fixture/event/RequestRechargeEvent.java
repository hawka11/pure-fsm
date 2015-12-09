package pure.fsm.core.test.fixture.event;

import pure.fsm.core.Transition;

import java.math.BigDecimal;
import java.util.Set;

public class RequestRechargeEvent implements TelcoEvent {

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
    public Transition accept(Transition last, TelcoEventVisitor visitor) {
        return visitor.visit(last, this);
    }

    public Set<String> getPinsToLock() {
        return pinsToLock;
    }
}
