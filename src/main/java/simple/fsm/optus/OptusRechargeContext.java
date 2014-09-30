package simple.fsm.optus;

import simple.fsm.core.Context;

import java.math.BigDecimal;

public class OptusRechargeContext implements Context {

    private final BigDecimal amount;

    public OptusRechargeContext(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
