package simple.fsm.optus;

import simple.fsm.core.accessor.CleanUpFinalisedStateMachines;
import simple.fsm.core.accessor.InMemoryStateMachineAccessor;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.core.template.StateMachineTemplate;
import simple.fsm.core.timeout.TimeoutTicker;
import simple.fsm.optus.state.InitialState;

import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

public class MainWithCleanup {

    public static void main(String[] args) throws Exception {
        final StateMachineAccessor accessor = new InMemoryStateMachineAccessor();
        final StateMachineTemplate template = new StateMachineTemplate(accessor);
        final TimeoutTicker timeoutTicker = new TimeoutTicker(accessor, template, 1, TimeUnit.SECONDS);
        final CleanUpFinalisedStateMachines cleaner = new CleanUpFinalisedStateMachines(accessor, 5, SECONDS);

        //create state machine
        final String stateMachineId = accessor.create(
                new InitialState(),
                new OptusRechargeContext());

        Thread.sleep(6000);

        //something should configure this to run periodically
        timeoutTicker.sendTimeOutTickerEvents();

        Thread.sleep(2000);

        //something should configure this to run periodically
        cleaner.checkForFinalizedStateMachinesAndCleanupIfRequired();

        Thread.sleep(2000);

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        System.out.println("Ending.... current state is: " + accessor.get(stateMachineId));
    }
}
