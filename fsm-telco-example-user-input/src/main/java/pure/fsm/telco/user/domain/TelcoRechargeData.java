package pure.fsm.telco.user.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.Context;
import pure.fsm.core.trait.Trait;
import pure.fsm.hazelcast.resource.DistributedLockResource;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class TelcoRechargeData implements Trait {

    private final Set<String> confirmedPins;

    @JsonCreator
    protected TelcoRechargeData(@JsonProperty("confirmedPins") Set<String> confirmedPins) {
        this.confirmedPins = confirmedPins;
    }

    public static TelcoRechargeData initialTelcoRechargeData() {
        return new TelcoRechargeData(newHashSet());
    }

    public void addConfirmedPin(String pin) {
        confirmedPins.add(pin);
    }

    public Set<String> getConfirmedPins() {
        //TODO: make this immutable but has serializing issues.
        return confirmedPins;
    }

    public Set<String> requestedPins(Context context) {
        return context.getTraitsOf(DistributedLockResource.class).stream()
                .flatMap(r -> r.getLockedKeys().stream())
                .collect(toSet());
    }

    public boolean allPinsConfirmed(Context context) {
        return requestedPins(context).stream()
                .filter(pin -> !getConfirmedPins().contains(pin))
                .count() == 0;
    }

    public Set<String> nonConfirmedPins(Context context) {
        return requestedPins(context).stream()
                .filter(pin -> !getConfirmedPins().contains(pin))
                .collect(toSet());
    }
}
