package pure.fsm.telcohazelcast;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pure.fsm.core.Context;
import pure.fsm.core.event.Event;
import pure.fsm.core.state.State;
import pure.fsm.hazelcast.resource.DistributedLockResource;
import pure.fsm.telco.TelcoRechargeContext;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class HzTelcoRechargeContext extends TelcoRechargeContext {

    protected HzTelcoRechargeContext(BaseContextBuilder baseContextBuilder, Set<String> acceptedPins) {
        super(baseContextBuilder, acceptedPins);
    }

    public HzTelcoRechargeContext() {
        this(initialTransition(), newHashSet());
    }

    @Override
    public Context transition(State newState, Event event) {

        return new HzTelcoRechargeContext(transitionWith(newState, event), getAcceptedPins());
    }

    @Override
    @JsonIgnore
    public Set<String> getRequestedPins() {
        return getResources().stream()
                .flatMap(r -> ((DistributedLockResource) r).getLockedKeys().stream())
                .collect(toSet());
    }
}
