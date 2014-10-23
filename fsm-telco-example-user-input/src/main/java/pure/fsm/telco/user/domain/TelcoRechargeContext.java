package pure.fsm.telco.user.domain;

import pure.fsm.core.BaseContext;
import pure.fsm.core.Context;
import pure.fsm.core.Resource;
import pure.fsm.hazelcast.resource.DistributedLockResource;

import java.time.LocalDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class TelcoRechargeContext extends BaseContext {

    private final Set<String> acceptedPins;

    protected TelcoRechargeContext(Set<Resource> resources, Exception e, String msg,
                                   LocalDateTime transitioned, Set<String> acceptedPins) {
        super(resources, e, msg, transitioned);
        this.acceptedPins = acceptedPins;
    }

    public TelcoRechargeContext() {
        this(newHashSet(), null, null, LocalDateTime.now(), newHashSet());
    }

    @Override
    public Context transition() {
        return new TelcoRechargeContext(getResources(), getException(), getMessage(), LocalDateTime.now(), acceptedPins);
    }

    public void addConfirmedPin(String pin) {
        acceptedPins.add(pin);
    }

    public Set<String> getConfirmedPins() {
        //TODO: make this immutable but has serializing issues.
        return acceptedPins;
    }

    public Set<String> getRequestedPins() {
        return getResources().stream()
                .flatMap(r -> ((DistributedLockResource) r).getLockedKeys().stream())
                .collect(toSet());
    }

    public boolean allPinsConfirmed() {
        return getRequestedPins().stream()
                .filter(pin -> !getConfirmedPins().contains(pin))
                .count() == 0;
    }

    public Set<String> getNonConfirmedPins() {
        return getRequestedPins().stream()
                .filter(pin -> !getConfirmedPins().contains(pin))
                .collect(toSet());
    }
}
