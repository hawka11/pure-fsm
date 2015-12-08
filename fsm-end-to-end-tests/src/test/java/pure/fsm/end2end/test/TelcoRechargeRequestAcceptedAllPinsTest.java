package pure.fsm.end2end.test;

import org.junit.Test;
import pure.fsm.core.StateMachineRepository;
import pure.fsm.end2end.event.RechargeAcceptedEvent;
import pure.fsm.end2end.event.RequestRechargeEvent;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Thread.sleep;
import static pure.fsm.end2end.test.TelcoStateAssertions.assert1stPinAccepted;
import static pure.fsm.end2end.test.TelcoStateAssertions.assert2ndPinAccepted;
import static pure.fsm.end2end.test.TelcoStateAssertions.assertInitialState;
import static pure.fsm.end2end.test.TelcoStateAssertions.assertRechargeRequestedState;


public class TelcoRechargeRequestAcceptedAllPinsTest extends BaseEnd2EndTest {

    public TelcoRechargeRequestAcceptedAllPinsTest(Supplier<StateMachineRepository> repository) {
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

        //Sometime later, another thread will send RechargeAcceptedEvent to sm
        //Probably invoked by a callback service
        ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent("555"));
        sleep(500);
        assert1stPinAccepted(stateMachineId, ops.getStateMachine(stateMachineId));

        ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent("666"));
        sleep(2000);
        assert2ndPinAccepted(stateMachineId, ops.getStateMachine(stateMachineId));
    }
}
