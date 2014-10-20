package simple.fsm.telcohazelcast;

import simple.fsm.core.Context;
import simple.fsm.core.Resource;
import simple.fsm.hazelcast.resource.DistributedLockResource;
import simple.fsm.telco.TelcoRechargeContext;

import java.time.LocalDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class HzTelcoRechargeContext extends TelcoRechargeContext {

    private HzTelcoRechargeContext(Set<Resource> resources, Exception e, String msg,
                                   LocalDateTime transitioned, Set<String> acceptedPins) {
        super(resources, e, msg, transitioned, acceptedPins);
    }

    public HzTelcoRechargeContext() {
        this(newHashSet(), null, null, LocalDateTime.now(), newHashSet());
    }

    @Override
    public Context transition() {
        return new HzTelcoRechargeContext(getResources(), getException(), getMessage(), LocalDateTime.now(), getAcceptedPins());
    }

    @Override
    public Set<String> getRequestedPins() {
        return getResources().stream()
                .flatMap(r -> ((DistributedLockResource) r).getLockedKeys().stream())
                .collect(toSet());
    }
}
