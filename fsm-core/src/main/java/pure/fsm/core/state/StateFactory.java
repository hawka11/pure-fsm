package pure.fsm.core.state;

public interface StateFactory {

    ErrorFinalState errorFinalState();

    TimedOutFinalState timedOutFinalState();

    SuccessFinalState successFinalState();

    SuccessFinalState userCanceled();

    <T extends State> T getStateByClass(Class<T> stateClass);
}
