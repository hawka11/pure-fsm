package pure.fsm.end2end.test;

import org.junit.Test;
import pure.fsm.core.Transition;
import pure.fsm.core.repository.StateMachineRepository;
import pure.fsm.end2end.TelcoRechargeContext;
import pure.fsm.end2end.event.RechargeAcceptedEvent;
import pure.fsm.end2end.event.RequestRechargeEvent;
import pure.fsm.end2end.state.InitialState;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;


public class SimpleSuccessTest extends BaseEnd2EndTest {

    public SimpleSuccessTest(Supplier<StateMachineRepository> repository) {
        super(repository);
    }

    @Test
    public void test() throws InterruptedException {
        StateMachineOperations ops = new StateMachineOperations(repository.get());

        //create state machine
        final String stateMachineId = ops.createStateMachineInInitialState();

        final Transition current = ops.getStateMachine(stateMachineId);

        assertInitialState(stateMachineId, current);

        //ops.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00"), newHashSet("555", "666")));
        //sleep(2000);

        //Sometime later, another thread will send RechargeAcceptedEvent to sm
        //Probably invoked by a callback via telco webservice
       // ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent("555"));
        //ops.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent("666"));
        //sleep(2000);

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        //ops.logCurrentState(stateMachineId);

    }

    private void assertInitialState(String stateMachineId, Transition current) {
        assertThat(current).isNotNull();
        assertThat(current.getState()).isNotNull();
        assertThat(current.getState().getClass()).isEqualTo(InitialState.class);
        assertThat(current.getEvent()).isNullOrEmpty();
        assertThat(current.previous().isPresent()).isEqualTo(false);
        assertThat(current.getContext()).isNotNull();
        assertThat(current.getContext().stateMachineId()).isEqualTo(stateMachineId);

        final List<TelcoRechargeContext> rechargeContexts = current.getContext().getContextsOfType(TelcoRechargeContext.class);
        assertThat(rechargeContexts).isNotNull();
        assertThat(rechargeContexts.size()).isEqualTo(1);
        assertThat(rechargeContexts.get(0).getAcceptedPins().size()).isEqualTo(2);

    }
}
