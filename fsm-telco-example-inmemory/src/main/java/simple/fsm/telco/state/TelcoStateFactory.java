package simple.fsm.telco.state;

import simple.fsm.core.state.BaseStateFactory;
import simple.fsm.core.state.State;
import simple.fsm.telco.guard.AllPinsRechargedAcceptedGuard;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class TelcoStateFactory extends BaseStateFactory {

    private final Map<Class<? extends State>, State> stateByStateClass = newHashMap();

    public TelcoStateFactory() {
        super();

        AllPinsRechargedAcceptedGuard guard = new AllPinsRechargedAcceptedGuard();

        stateByStateClass.put(InitialState.class, new InitialState());
        stateByStateClass.put(RechargeRequestedState.class, new RechargeRequestedState(guard));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends State> T internalGetStateByClass(Class<T> stateClass) {
        return (T) stateByStateClass.get(stateClass);
    }
}
