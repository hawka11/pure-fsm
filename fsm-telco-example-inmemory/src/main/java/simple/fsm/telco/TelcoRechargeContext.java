package simple.fsm.telco;

import simple.fsm.core.BaseContext;
import simple.fsm.core.Context;
import simple.fsm.core.Resource;

import java.time.LocalDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class TelcoRechargeContext extends BaseContext {

    private TelcoRechargeContext(Set<Resource> resources, Exception e, String msg, LocalDateTime transitioned) {

        super(resources, e, msg, transitioned);
    }

    public TelcoRechargeContext() {
        super(newHashSet(), null, null, LocalDateTime.now());
    }

    @Override
    public Context transition() {
        return new TelcoRechargeContext(getResources(), getException(), getMessage(), LocalDateTime.now());
    }
}
