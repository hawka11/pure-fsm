package pure.fsm.telcohazelcast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.Context;
import pure.fsm.hazelcast.resource.DistributedLockResource;
import pure.fsm.telco.TelcoRechargeTrait;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class HzTelcoRechargeTrait extends TelcoRechargeTrait {

    @JsonCreator
    private HzTelcoRechargeTrait(@JsonProperty("acceptedPins") Set<String> acceptedPins) {
        super(acceptedPins);
    }

    public static HzTelcoRechargeTrait initialTelcoRecharge() {
        return new HzTelcoRechargeTrait(newHashSet());
    }

    public static Set<String> getRequestedPins(Context context) {
        return context.getTraitsOf(DistributedLockResource.class).stream()
                .flatMap(r -> r.getLockedKeys().stream())
                .collect(toSet());
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
