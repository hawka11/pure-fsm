package pure.fsm.telco;

import pure.fsm.core.Context;
import pure.fsm.core.context.BaseContext;
import pure.fsm.core.state.State;
import pure.fsm.telco.state.LockedPinResource;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class TelcoRechargeContext extends BaseContext {

    private final Set<String> acceptedPins;

    protected TelcoRechargeContext(BaseContextBuilder baseContextBuilder, Set<String> acceptedPins) {
        super(baseContextBuilder);
        this.acceptedPins = acceptedPins;
    }

    public TelcoRechargeContext() {
        this(initialTransition(), newHashSet());
    }

    @Override
    public Context transition(State newState) {

        return new TelcoRechargeContext(transitionWith(newState), acceptedPins);
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
