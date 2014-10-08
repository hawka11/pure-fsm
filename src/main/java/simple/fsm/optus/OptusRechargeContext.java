package simple.fsm.optus;

import simple.fsm.core.Context;
import simple.fsm.core.Resource;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class OptusRechargeContext implements Context {

    private final Set<Resource> resources = newHashSet();

    @Override
    public void addResource(Resource resource) {
        resources.add(resource);
    }

    @Override
    public void unlockResources() {
        resources.forEach(Resource::unlock);
    }
}
