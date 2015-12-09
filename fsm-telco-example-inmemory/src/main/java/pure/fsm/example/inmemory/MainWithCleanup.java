package pure.fsm.example.inmemory;

import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.java.test.fixture.state.BaseTelcoState.TIMEOUT_SECS;

public class MainWithCleanup {

    public static void main(String[] args) throws Exception {
        StateMachineOperations ops = new StateMachineOperations();

        //create state machine
        final String stateMachineId = ops.createStateMachineInInitialState();
        Thread.sleep(SECONDS.toMillis(TIMEOUT_SECS + 1));

        //something should configure this to run periodically
        ops.timeoutEventTicker.tick();
        Thread.sleep(6000);

        //something should configure this to run periodically
        ops.cleaner.checkForFinalizedStateMachinesAndCleanupIfRequired();

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        ops.logCurrentState(stateMachineId);
    }
}
