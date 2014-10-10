package simple.fsm.optus.state;

import simple.fsm.core.state.BaseStateFactory;
import simple.fsm.core.state.State;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class OptusStateFactory extends BaseStateFactory {

    private final Map<Class<? extends State>, State> stateByStateClass = newHashMap();

    public OptusStateFactory() {
        super();
        stateByStateClass.put(InitialState.class, new InitialState());
        stateByStateClass.put(RechargeRequestedState.class, new RechargeRequestedState());
    }

    @Override
    protected <T extends State> T internalGetStateByClass(Class<T> stateClass) {
        return (T) stateByStateClass.get(stateClass);
    }
}
