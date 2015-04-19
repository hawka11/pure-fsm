package pure.fsm.telco;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.telco.state.LockedPinResource;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static pure.fsm.core.context.MostRecentTrait.mostRecentOf;

public class TelcoRechargeContext implements Context {

    private final Set<String> acceptedPins;

    @JsonCreator
    protected TelcoRechargeContext(@JsonProperty("acceptedPins") Set<String> acceptedPins) {
        this.acceptedPins = acceptedPins;
    }

    public static TelcoRechargeContext initialTelcoRecharge() {
        return new TelcoRechargeContext(newHashSet());
    }

    public TelcoRechargeContext addAcceptedPin(String pin) {
        acceptedPins.add(pin);
        return this;
    }

    public Set<String> getAcceptedPins() {
        return acceptedPins;
    }

    public static Set<String> getRequestedPins(Transition transition) {
        return mostRecentOf(transition, LockedPinResource.class)
                .map(LockedPinResource::getPinsToLock)
                .orElse(newHashSet());
    }
}
