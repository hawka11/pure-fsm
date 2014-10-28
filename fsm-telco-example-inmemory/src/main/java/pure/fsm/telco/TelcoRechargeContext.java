package pure.fsm.telco;

import pure.fsm.core.BaseContext;
import pure.fsm.core.Context;
import pure.fsm.core.Resource;
import pure.fsm.core.state.State;
import pure.fsm.telco.state.LockedPinResource;

import java.time.LocalDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class TelcoRechargeContext extends BaseContext {

    private final Set<String> acceptedPins;

    protected TelcoRechargeContext(String stateMachineId, Set<Resource> resources, Exception e, String msg,
                                   LocalDateTime transitioned, State current, Context previous, Set<String> acceptedPins) {
        super(stateMachineId, resources, e, msg, transitioned, current, previous);
        this.acceptedPins = acceptedPins;
    }

    public TelcoRechargeContext() {
        this(null, newHashSet(), null, null, LocalDateTime.now(), null, null, newHashSet());
    }

    @Override
    public Context transition(State newState) {

        return new TelcoRechargeContext(getStateMachineId(), getResources(), getException(),
                getMessage(), LocalDateTime.now(), newState, this, acceptedPins);
    }

    public void addAcceptedPin(String pin) {
        acceptedPins.add(pin);
    }

    public Set<String> getAcceptedPins() {
        //TODO: make immutable
        return acceptedPins;
    }

    public Set<String> getRequestedPins() {
        return getResources().stream()
                .flatMap(r -> ((LockedPinResource) r).getPinsToLock().stream())
                .collect(toSet());
    }
}
