package pure.fsm.telco.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pure.fsm.core.BaseContext;
import pure.fsm.core.Context;
import pure.fsm.core.Resource;
import pure.fsm.core.state.State;
import pure.fsm.hazelcast.resource.DistributedLockResource;

import java.time.LocalDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class TelcoRechargeContext extends BaseContext {

    private final Set<String> confirmedPins;

    protected TelcoRechargeContext(String stateMachineId, Set<Resource> resources, Exception e, String msg,
                                   LocalDateTime transitioned, State current, Context previous, Set<String> confirmedPins) {
        super(stateMachineId, resources, e, msg, transitioned, current, previous);
        this.confirmedPins = confirmedPins;
    }

    public TelcoRechargeContext() {
        this(null, newHashSet(), null, null, LocalDateTime.now(), null, null, newHashSet());
    }

    @Override
    public Context transition(State newState) {
        return new TelcoRechargeContext(getStateMachineId(), getResources(), getException(),
                getMessage(), LocalDateTime.now(), newState, this, confirmedPins);
    }

    public void addConfirmedPin(String pin) {
        confirmedPins.add(pin);
    }

    public Set<String> getConfirmedPins() {
        //TODO: make this immutable but has serializing issues.
        return confirmedPins;
    }

    public Set<String> requestedPins() {
        return getResources().stream()
                .flatMap(r -> ((DistributedLockResource) r).getLockedKeys().stream())
                .collect(toSet());
    }

    public boolean allPinsConfirmed() {
        return requestedPins().stream()
                .filter(pin -> !getConfirmedPins().contains(pin))
                .count() == 0;
    }

    public Set<String> nonConfirmedPins() {
        return requestedPins().stream()
                .filter(pin -> !getConfirmedPins().contains(pin))
                .collect(toSet());
    }
}
