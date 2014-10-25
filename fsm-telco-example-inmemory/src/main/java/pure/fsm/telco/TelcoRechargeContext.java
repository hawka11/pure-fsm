package pure.fsm.telco;

import com.google.common.collect.ImmutableSet;
import pure.fsm.core.BaseContext;
import pure.fsm.core.Context;
import pure.fsm.core.Resource;
import pure.fsm.telco.state.LockedPinResource;

import java.time.LocalDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class TelcoRechargeContext extends BaseContext {

    private final Set<String> acceptedPins;

    protected TelcoRechargeContext(String stateMachineId, Set<Resource> resources, Exception e, String msg,
                                 LocalDateTime transitioned, Set<String> acceptedPins) {
        super(stateMachineId, resources, e, msg, transitioned);
        this.acceptedPins = acceptedPins;
    }

    public TelcoRechargeContext() {
        this(null, newHashSet(), null, null, LocalDateTime.now(), newHashSet());
    }

    @Override
    public Context transition() {
        return new TelcoRechargeContext(getStateMachineId(), getResources(), getException(), getMessage(), LocalDateTime.now(), acceptedPins);
    }

    public void addAcceptedPin(String pin) {
        acceptedPins.add(pin);
    }

    public Set<String> getAcceptedPins() {
        return ImmutableSet.copyOf(acceptedPins);
    }

    public Set<String> getRequestedPins() {
        return getResources().stream()
                .flatMap(r -> ((LockedPinResource) r).getPinsToLock().stream())
                .collect(toSet());
    }
}
