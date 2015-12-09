package pure.fsm.end2end.test;

import org.junit.Test;
import pure.fsm.core.StateMachineRepository;
import pure.fsm.end2end.event.RequestRechargeEvent;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static pure.fsm.end2end.test.StateMachineOperations.KEEP_AROUND_B4_REMOVING;
import static pure.fsm.end2end.test.TelcoStateAssertions.assertInitialState;
import static pure.fsm.end2end.test.TelcoStateAssertions.assertRechargeRequestedState;
import static pure.fsm.end2end.test.TelcoStateAssertions.assertStateMachineHasTimedout;


public class TelcoRechargeTimeoutAndCleanupTest extends BaseEnd2EndTest {

    public TelcoRechargeTimeoutAndCleanupTest(Supplier<StateMachineRepository> repository) {
        super(repository);
    }

    private static final int STATE_TIMEOUT = 1000;

    @Test
    public void runTest() throws InterruptedException {
        StateMachineOperations ops = new StateMachineOperations(repository.get());

        //create state machine
        final String stateMachineId = ops.createStateMachineInInitialState();
        assertInitialState(stateMachineId, ops.getStateMachine(stateMachineId));

        ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555", "666")));
        sleep(500);
        assertRechargeRequestedState(stateMachineId, ops.getStateMachine(stateMachineId));

        sleep(STATE_TIMEOUT);

        //something should configure this to run periodically
        ops.getEventTicker().tick();
        assertStateMachineHasTimedout(stateMachineId, ops.getStateMachine(stateMachineId));

        sleep(KEEP_AROUND_B4_REMOVING);

        //something should configure this to run periodically
        ops.getCleaner().checkForFinalizedStateMachinesAndCleanupIfRequired();
        assertThat(ops.getStateMachine(stateMachineId)).isNull();
    }
}
