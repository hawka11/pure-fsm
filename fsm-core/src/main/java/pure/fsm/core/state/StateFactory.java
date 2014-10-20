package pure.fsm.core.state;

import pure.fsm.core.Context;

public interface StateFactory {

    ErrorFinalState errorFinalState();

    TimedOutFinalState timedOutFinalState();

    SuccessFinalState successFinalState();

    SuccessFinalState userCanceled(Context context);

    <T extends State> T getStateByClass(Class<T> stateClass);
}
