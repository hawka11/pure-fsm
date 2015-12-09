package pure.fsm.end2end.test;

import pure.fsm.core.FinalState;
import pure.fsm.core.Transition;
import pure.fsm.java.test.fixture.TelcoRechargeContext;
import pure.fsm.java.test.fixture.event.RechargeAcceptedEvent;
import pure.fsm.java.test.fixture.event.RequestRechargeEvent;
import pure.fsm.java.test.fixture.event.TelcoEvent;
import pure.fsm.java.test.fixture.event.TimeoutTickEvent;
import pure.fsm.java.test.fixture.state.InitialState;
import pure.fsm.java.test.fixture.state.RechargeRequestedState;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static pure.fsm.core.FinalState.TIMEOUT_ERROR_FINAL_STATE;

public class TelcoStateAssertions {

    public static void assert2ndPinAccepted(String stateMachineId, Transition current) {
        assertEventAndState(stateMachineId, current, FinalState.SuccessFinalState.class, RechargeAcceptedEvent.class);

        final List<TelcoRechargeContext> rechargeContexts = current.getContext().getContextsOfType(TelcoRechargeContext.class);
        assertThat(rechargeContexts).isNotNull();
        assertThat(rechargeContexts.size()).isEqualTo(3);

        final TelcoRechargeContext mostRecent = current.getContext().mostRecentOf(TelcoRechargeContext.class).get();
        assertThat(mostRecent.getAcceptedPins().size()).isEqualTo(2);

        final Set<String> requestedPins = TelcoRechargeContext.getRequestedPins(current.getContext());
        assertThat(requestedPins.size()).isEqualTo(2);
    }

    public static void assertRechargeRequestedState(String stateMachineId, Transition current) {

        assertEventAndState(stateMachineId, current, RechargeRequestedState.class, RequestRechargeEvent.class);

        final List<TelcoRechargeContext> rechargeContexts = current.getContext().getContextsOfType(TelcoRechargeContext.class);
        assertThat(rechargeContexts).isNotNull();
        assertThat(rechargeContexts.size()).isEqualTo(1);

        final TelcoRechargeContext mostRecent = current.getContext().mostRecentOf(TelcoRechargeContext.class).get();
        assertThat(mostRecent.getAcceptedPins().size()).isEqualTo(0);

        final Set<String> requestedPins = TelcoRechargeContext.getRequestedPins(current.getContext());
        assertThat(requestedPins.size()).isEqualTo(2);
    }

    public static void assertStateMachineHasTimedout(String stateMachineId, Transition current) {

        assertEventAndState(stateMachineId, current, TIMEOUT_ERROR_FINAL_STATE.getClass(), TimeoutTickEvent.class);

        final List<TelcoRechargeContext> rechargeContexts = current.getContext().getContextsOfType(TelcoRechargeContext.class);
        assertThat(rechargeContexts).isNotNull();
        assertThat(rechargeContexts.size()).isEqualTo(1);

        final TelcoRechargeContext mostRecent = current.getContext().mostRecentOf(TelcoRechargeContext.class).get();
        assertThat(mostRecent.getAcceptedPins().size()).isEqualTo(0);

        final Set<String> requestedPins = TelcoRechargeContext.getRequestedPins(current.getContext());
        assertThat(requestedPins.size()).isEqualTo(2);
    }

    public static void assert1stPinAccepted(String stateMachineId, Transition current) {

        assertEventAndState(stateMachineId, current, RechargeRequestedState.class, RechargeAcceptedEvent.class);

        final List<TelcoRechargeContext> rechargeContexts = current.getContext().getContextsOfType(TelcoRechargeContext.class);
        assertThat(rechargeContexts).isNotNull();
        assertThat(rechargeContexts.size()).isEqualTo(2);

        final TelcoRechargeContext mostRecent = current.getContext().mostRecentOf(TelcoRechargeContext.class).get();
        assertThat(mostRecent.getAcceptedPins().size()).isEqualTo(1);

        final Set<String> requestedPins = TelcoRechargeContext.getRequestedPins(current.getContext());
        assertThat(requestedPins.size()).isEqualTo(2);
    }

    public static void assertEventAndState(String stateMachineId, Transition current,
                                           Class<?> expectedState,
                                           Class<? extends TelcoEvent> expectedEvent) {

        assertThat(current).isNotNull();
        assertThat(current.getState()).isNotNull();
        assertThat(current.getState().getClass()).isEqualTo(expectedState);
        assertThat(current.getEvent()).isNotEmpty();
        assertThat(current.getEvent()).isEqualTo(expectedEvent.getName());
        assertThat(current.previous().isPresent()).isEqualTo(true);
        assertThat(current.getContext()).isNotNull();
        assertThat(current.getContext().stateMachineId()).isEqualTo(stateMachineId);
    }

    public static void assertInitialState(String stateMachineId, Transition current) {
        assertThat(current).isNotNull();
        assertThat(current.getState()).isNotNull();
        assertThat(current.getState().getClass()).isEqualTo(InitialState.class);
        assertThat(current.getEvent()).isEqualTo("InitialEvent");
        assertThat(current.previous().isPresent()).isEqualTo(false);
        assertThat(current.getContext()).isNotNull();
        assertThat(current.getContext().stateMachineId()).isEqualTo(stateMachineId);

        final List<TelcoRechargeContext> rechargeContexts = current.getContext().getContextsOfType(TelcoRechargeContext.class);
        assertThat(rechargeContexts).isNotNull();
        assertThat(rechargeContexts.size()).isEqualTo(1);
        assertThat(rechargeContexts.get(0).getAcceptedPins().size()).isEqualTo(0);

        final Set<String> requestedPins = TelcoRechargeContext.getRequestedPins(current.getContext());
        assertThat(requestedPins.size()).isEqualTo(0);
    }
}
