package pure.fsm.core.state;

import pure.fsm.core.Transition;

public interface StateFactory {

    ErrorFinalState errorFinalState();

    TimedOutFinalState timedOutFinalState();

    SuccessFinalState successFinalState();

    SuccessFinalState userCanceled(Transition transition);

    <T extends State> T getStateByClass(Class<T> stateClass);
}
