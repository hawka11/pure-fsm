package simple.fsm.core.state;

import com.google.common.base.Preconditions;
import simple.fsm.core.Context;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public abstract class BaseStateFactory implements StateFactory {

    private final Map<Class<? extends State>, State> stateByStateClass = newHashMap();

    protected BaseStateFactory() {
        stateByStateClass.put(TimedOutFinalState.class, new TimedOutFinalState());
        stateByStateClass.put(SuccessFinalState.class, new SuccessFinalState());
        stateByStateClass.put(ErrorFinalState.class, new ErrorFinalState());
    }

    @Override
    public ErrorFinalState errorFinalState() {
        return getStateByClass(ErrorFinalState.class);
    }

    @Override
    public TimedOutFinalState timedOutFinalState() {
        return getStateByClass(TimedOutFinalState.class);
    }

    @Override
    public SuccessFinalState successFinalState() {
        return getStateByClass(SuccessFinalState.class);
    }

    @Override
    public SuccessFinalState userCanceled(Context context) {
        context.setMessage("USER_CANCELED");
        return new SuccessFinalState();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends State> T getStateByClass(Class<T> stateClass) {
        T state = (T) stateByStateClass.get(stateClass);
        if (state == null) {
            state = internalGetStateByClass(stateClass);
        }
        return Preconditions.checkNotNull(state);
    }

    protected abstract <T extends State> T internalGetStateByClass(Class<T> stateClass);
}
