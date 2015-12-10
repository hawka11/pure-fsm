package pure.fsm.end2end.test;

import org.junit.Test;
import pure.fsm.core.TransitionRepository;
import pure.fsm.java.test.fixture.event.RequestRechargeEvent;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static pure.fsm.end2end.test.StateMachineOperations.KEEP_AROUND_B4_REMOVING;
import static pure.fsm.end2end.test.TelcoStateAssertions.assertInitialState;
import static pure.fsm.end2end.test.TelcoStateAssertions.assertRechargeRequestedState;
import static pure.fsm.end2end.test.TelcoStateAssertions.assertStateMachineHasTimedout;
import static pure.fsm.java.test.fixture.state.BaseTelcoState.TIMEOUT_SECS;


public class TelcoRechargeTimeoutAndCleanupTest extends BaseEnd2EndTest {

    public TelcoRechargeTimeoutAndCleanupTest(Supplier<TransitionRepository> repository) {
        super(repository);
    }

    @Test
    public void runTest() throws InterruptedException {
        StateMachineOperations ops = new StateMachineOperations(repository.get());

        //create state machine
        final String stateMachineId = ops.createStateMachineInInitialState();
        assertInitialState(stateMachineId, ops.getStateMachine(stateMachineId));

        ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555", "666")));
        sleep(500);
        assertRechargeRequestedState(stateMachineId, ops.getStateMachine(stateMachineId));

        sleep(SECONDS.toMillis(TIMEOUT_SECS) + 500);

        //something should configure this to run periodically
        ops.timeoutEventTicker.tick();
        assertStateMachineHasTimedout(stateMachineId, ops.getStateMachine(stateMachineId));

        sleep(KEEP_AROUND_B4_REMOVING);

        //something should configure this to run periodically
        ops.cleaner.checkForFinalizedStateMachinesAndCleanupIfRequired();
        assertThat(ops.getStateMachine(stateMachineId)).isNull();
    }
}
