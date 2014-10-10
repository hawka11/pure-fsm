package simple.fsm.optus;

import simple.fsm.core.BaseContext;
import simple.fsm.core.Context;
import simple.fsm.core.Resource;

import java.time.LocalDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class OptusRechargeContext extends BaseContext {

    private OptusRechargeContext(Set<Resource> resources, Exception e, String msg, LocalDateTime transitioned) {
        super(resources, e, msg, transitioned);
    }

    public OptusRechargeContext() {
        super(newHashSet(), null, null, LocalDateTime.now());
    }

    @Override
    public Context transition() {
        return new OptusRechargeContext(getResources(), getException(), getMessage(), LocalDateTime.now());
    }
}
