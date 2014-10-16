package simple.fsm.optus;

public class MainWithCleanup {

    public static void main(String[] args) throws Exception {
        StateMachineOperations ops = new StateMachineOperations();

        //create state machine
        final String stateMachineId = ops.createStateMachineInInitialState();
        Thread.sleep(6000);

        //something should configure this to run periodically
        ops.getTimeoutTicker().sendTimeOutTickerEvents();
        Thread.sleep(2000);

        //something should configure this to run periodically
        ops.getCleaner().checkForFinalizedStateMachinesAndCleanupIfRequired();
        Thread.sleep(2000);

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        ops.logCurrentState(stateMachineId);
    }
}
