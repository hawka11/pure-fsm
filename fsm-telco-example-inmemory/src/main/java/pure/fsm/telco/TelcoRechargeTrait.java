package pure.fsm.telco;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.Context;
import pure.fsm.core.trait.Trait;
import pure.fsm.telco.state.LockedPinResource;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class TelcoRechargeTrait implements Trait {

    private final Set<String> acceptedPins;

    @JsonCreator
    protected TelcoRechargeTrait(@JsonProperty("acceptedPins") Set<String> acceptedPins) {
        this.acceptedPins = acceptedPins;
    }

    public static TelcoRechargeTrait initialTelcoRecharge() {
        return new TelcoRechargeTrait(newHashSet());
    }

    public TelcoRechargeTrait addAcceptedPin(String pin) {
        acceptedPins.add(pin);
        return this;
    }

    public Set<String> getAcceptedPins() {
        return acceptedPins;
    }

    public static Set<String> getRequestedPins(Context context) {
        return context.getTraitsOf(LockedPinResource.class).stream()
                .flatMap(r -> r.getPinsToLock().stream())
                .collect(toSet());
    }
}
