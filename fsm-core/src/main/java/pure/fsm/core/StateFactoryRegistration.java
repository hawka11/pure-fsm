package pure.fsm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.state.StateFactory;

import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Optional.ofNullable;

public class StateFactoryRegistration {

    private final static Logger LOG = LoggerFactory.getLogger(StateFactoryRegistration.class);

    private static final Map<String, StateFactory> stateFactoryByKlass = newHashMap();

    public static synchronized void registerStateFactory(StateFactory stateFactory) {
        stateFactoryByKlass.put(stateFactory.getClass().getName(), stateFactory);
    }

    @SuppressWarnings("unchecked")
    public static synchronized Optional<StateFactory> getStateFactory(String stateFactoryKlass) {
        try {
            return getStateFactory((Class<StateFactory>) Class.forName(stateFactoryKlass));
        } catch (ClassNotFoundException e) {
            LOG.error("Could not find state factory of class [{}]", stateFactoryKlass);
            return Optional.empty();
        }
    }

    public static synchronized Optional<StateFactory> getStateFactory(Class<? extends StateFactory> stateFactoryKlass) {
        return ofNullable(stateFactoryByKlass.get(stateFactoryKlass.getName()));
    }
}
