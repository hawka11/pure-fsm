package simple.fsm.optus;

public class MainWithCleanup {

    public static void main(String[] args) throws Exception {
        StateMachineOperations operations = new StateMachineOperations();

        //create state machine
        final String stateMachineId = operations.createStateMachineInInitialState();
        Thread.sleep(6000);

        //something should configure this to run periodically
        operations.getTimeoutTicker().sendTimeOutTickerEvents();
        Thread.sleep(2000);

        //something should configure this to run periodically
        operations.getCleaner().checkForFinalizedStateMachinesAndCleanupIfRequired();
        Thread.sleep(2000);

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        System.out.println("Ending.... current state is: " +
                operations.getStateMachine(stateMachineId).getCurrentState().getClass().getSimpleName());
    }
}
