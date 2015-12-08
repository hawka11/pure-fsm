package pure.fsm.end2end.test;

import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;
import pure.fsm.core.timeout.TimeoutTickEvent;
import pure.fsm.core.state.State;
import pure.fsm.core.state.SuccessFinalState;
import pure.fsm.core.state.TimedOutFinalState;
import pure.fsm.end2end.TelcoRechargeContext;
import pure.fsm.end2end.event.RechargeAcceptedEvent;
import pure.fsm.end2end.event.RequestRechargeEvent;
import pure.fsm.end2end.state.InitialState;
import pure.fsm.end2end.state.RechargeRequestedState;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TelcoStateAssertions {

    public static void assert2ndPinAccepted(String stateMachineId, Transition current) {
        assertEventAndState(stateMachineId, current, SuccessFinalState.class, RechargeAcceptedEvent.class);

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

        assertEventAndState(stateMachineId, current, TimedOutFinalState.class, TimeoutTickEvent.class);

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
                                           Class<? extends State> expectedState,
                                           Class<? extends Event> expectedEvent) {

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
        assertThat(current.getEvent()).isNullOrEmpty();
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
