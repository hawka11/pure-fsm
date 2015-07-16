package pure.fsm.telco.user.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableSet;
import pure.fsm.core.Context;
import pure.fsm.repository.hazelcast.resource.DistributedLockResource;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class TelcoRechargeData {

    @JsonSerialize
    private final Set<String> confirmedPins;

    @JsonCreator
    protected TelcoRechargeData(@JsonProperty("confirmedPins") Set<String> confirmedPins) {
        this.confirmedPins = confirmedPins;
    }

    public static TelcoRechargeData initialTelcoRechargeData() {
        return new TelcoRechargeData(newHashSet());
    }

    public TelcoRechargeData addConfirmedPin(String pin) {
        final Set<String> newPins = newHashSet(confirmedPins);
        newPins.add(pin);
        return new TelcoRechargeData(newPins);
    }

    @JsonIgnore
    public Set<String> getConfirmedPins() {
        return ImmutableSet.copyOf(confirmedPins);
    }

    public Set<String> requestedPins(Context context) {
        return context.getContextsOfType(DistributedLockResource.class).stream()
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
