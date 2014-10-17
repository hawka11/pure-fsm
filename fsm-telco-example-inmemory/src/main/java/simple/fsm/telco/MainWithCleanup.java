package simple.fsm.telco;

public class MainWithCleanup {

    public static void main(String[] args) throws Exception {
        StateMachineOperations ops = new StateMachineOperations();

        //create state machine
        final String stateMachineId = ops.createStateMachineInInitialState();
        Thread.sleep(5000);

        //something should configure this to run periodically
        ops.getTimeoutTicker().sendTimeOutTickerEvents();
        Thread.sleep(6000);

        //something should configure this to run periodically
        ops.getCleaner().checkForFinalizedStateMachinesAndCleanupIfRequired();

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        ops.logCurrentState(stateMachineId);
    }
}
