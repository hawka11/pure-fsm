package pure.fsm.telcohazelcast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.Context;
import pure.fsm.repository.hazelcast.resource.DistributedLockResource;
import pure.fsm.telco.TelcoRechargeContext;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class HzTelcoRechargeContext extends TelcoRechargeContext {

    @JsonCreator
    private HzTelcoRechargeContext(@JsonProperty("acceptedPins") Set<String> acceptedPins) {
        super(acceptedPins);
    }

    public static HzTelcoRechargeContext initialTelcoRecharge() {
        return new HzTelcoRechargeContext(newHashSet());
    }

    public static Set<String> getRequestedPins(Context context) {
        return context.getContextsOfType(DistributedLockResource.class).stream()
                .flatMap(r -> r.getLockedKeys().stream())
                .collect(toSet());
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
