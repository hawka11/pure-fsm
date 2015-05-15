package pure.fsm.core.fixture;

import pure.fsm.core.state.ErrorFinalState;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;
import pure.fsm.core.state.SuccessFinalState;
import pure.fsm.core.state.TimedOutFinalState;

public class TestStateFactory implements StateFactory {

    @Override
    public ErrorFinalState errorFinalState() {
        return null;
    }

    @Override
    public TimedOutFinalState timedOutFinalState() {
        return null;
    }

    @Override
    public SuccessFinalState successFinalState() {
        return null;
    }

    @Override
    public SuccessFinalState userCanceled() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends State> T getStateByClass(Class<T> stateClass) {
        if(TestNonFinalState.class.equals(stateClass)) {
            return (T) new TestNonFinalState();
        }
        return null;
    }
}
