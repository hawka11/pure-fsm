package pure.fsm.telco.user.domain;

import pure.fsm.core.Context;
import pure.fsm.core.context.BaseContext;
import pure.fsm.core.state.State;
import pure.fsm.hazelcast.resource.DistributedLockResource;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class TelcoRechargeContext extends BaseContext {

    private final Set<String> confirmedPins;

    protected TelcoRechargeContext(BaseContextBuilder baseContextBuilder, Set<String> confirmedPins) {
        super(baseContextBuilder);
        this.confirmedPins = confirmedPins;
    }

    public TelcoRechargeContext() {
        this(initialTransition(), newHashSet());
    }

    @Override
    public Context transition(State newState) {

        return new TelcoRechargeContext(transitionWith(newState), confirmedPins);
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
