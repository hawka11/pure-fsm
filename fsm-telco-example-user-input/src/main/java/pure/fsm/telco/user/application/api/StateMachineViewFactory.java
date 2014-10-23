package pure.fsm.telco.user.application.api;

import io.dropwizard.views.View;
import pure.fsm.core.StateMachine;
import pure.fsm.core.state.State;
import pure.fsm.telco.user.domain.TelcoRechargeContext;
import pure.fsm.telco.user.domain.state.InitialState;
import pure.fsm.telco.user.domain.state.WaitingForAcceptance;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.stream.Collectors.toSet;

public class StateMachineViewFactory {

    private final static Map<Class<? extends State>, Class<? extends StateMachineView>> viewByStateMachineState;

    static {
        viewByStateMachineState = newHashMap();
        viewByStateMachineState.put(InitialState.class, InitialView.class);
        viewByStateMachineState.put(WaitingForAcceptance.class, WatingView.class);
    }

    public Optional<View> getViewFor(StateMachine sm) {

        try {
            Class<? extends StateMachineView> view = viewByStateMachineState.get(sm.getCurrentState().getClass());
            if (view != null) {
                StateMachineView smView = view.newInstance();
                smView.setStateMachine(sm);
                return Optional.of(smView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    static abstract class StateMachineView extends View {

        private StateMachine stateMachine;

        protected StateMachineView(String templateName) {
            super(templateName);
        }

        void setStateMachine(StateMachine sm) {
            this.stateMachine = sm;
        }

        public StateMachine getStateMachine() {
            return stateMachine;
        }
    }

    static class WatingView extends StateMachineView {
        protected WatingView() {
            super("waiting.mustache");
        }

        public Set<String> getConfirmedPins() {
            return ((TelcoRechargeContext) getStateMachine().getContext()).getConfirmedPins();
        }

        public Set<String> getWaitingPins() {
            return ((TelcoRechargeContext) getStateMachine().getContext()).getRequestedPins().stream()
                    .filter(pin -> !getConfirmedPins().contains(pin))
                    .collect(toSet());
        }
    }

    static class InitialView extends StateMachineView {
        protected InitialView() {
            super("initial.mustache");
        }
    }
}
