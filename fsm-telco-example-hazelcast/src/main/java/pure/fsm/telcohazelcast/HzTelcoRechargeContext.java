package pure.fsm.telcohazelcast;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pure.fsm.core.Context;
import pure.fsm.core.Resource;
import pure.fsm.core.state.State;
import pure.fsm.hazelcast.resource.DistributedLockResource;
import pure.fsm.telco.TelcoRechargeContext;

import java.time.LocalDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class HzTelcoRechargeContext extends TelcoRechargeContext {

    private HzTelcoRechargeContext(String stateMachineId, Set<Resource> resources, Exception e, String msg,
                                   LocalDateTime transitioned, State current, Context previous, Set<String> acceptedPins) {
        super(stateMachineId, resources, e, msg, transitioned, current, previous, acceptedPins);
    }

    public HzTelcoRechargeContext() {
        this(null, newHashSet(), null, null, LocalDateTime.now(), null, null, newHashSet());
    }

    @Override
    public Context transition(State newState) {
        return new HzTelcoRechargeContext(getStateMachineId(), getResources(), getException(),
                getMessage(), LocalDateTime.now(), newState, this, getAcceptedPins());
    }

    @Override
    @JsonIgnore
    public Set<String> getRequestedPins() {
        return getResources().stream()
                .flatMap(r -> ((DistributedLockResource) r).getLockedKeys().stream())
                .collect(toSet());
    }
}
