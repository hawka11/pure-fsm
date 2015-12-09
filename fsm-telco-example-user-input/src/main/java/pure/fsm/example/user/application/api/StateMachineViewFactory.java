package pure.fsm.example.user.application.api;

import io.dropwizard.views.View;
import pure.fsm.core.FinalState.ErrorFinalState;
import pure.fsm.core.FinalState.SuccessFinalState;
import pure.fsm.core.FinalState.TimeoutErrorFinalState;
import pure.fsm.core.Transition;
import pure.fsm.example.user.domain.TelcoRechargeData;
import pure.fsm.example.user.domain.state.InitialState;
import pure.fsm.example.user.domain.state.WaitingForConfirmationState;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;

public class StateMachineViewFactory {

    private final static Map<Class<?>, Class<? extends StateMachineView>> viewByStateMachineState;

    static {
        viewByStateMachineState = newHashMap();

        viewByStateMachineState.put(InitialState.class, InitialView.class);
        viewByStateMachineState.put(WaitingForConfirmationState.class, WatingView.class);
        viewByStateMachineState.put(SuccessFinalState.class, SuccessView.class);

        viewByStateMachineState.put(ErrorFinalState.class, ErrorView.class);
        viewByStateMachineState.put(TimeoutErrorFinalState.class, ErrorView.class);
    }

    public Optional<View> getViewFor(Transition transition) {

        try {
            Class<? extends StateMachineView> view = viewByStateMachineState.get(transition.getState().getClass());
            if (view != null) {
                StateMachineView smView = view.newInstance();
                smView.setTransition(transition);
                return Optional.of(smView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    static abstract class StateMachineView extends View {

        private Transition transition;

        protected StateMachineView(String templateName) {
            super(templateName);
        }

        void setTransition(Transition transition) {
            this.transition = transition;
        }

        public Transition getTransition() {
            return transition;
        }

        public String getStateMachineId() {
            return transition.getContext().stateMachineId();
        }
    }

    static class WatingView extends StateMachineView {
        protected WatingView() {
            super("waiting.mustache");
        }

        public Set<String> nonConfirmedPins() {
            return data().nonConfirmedPins(getTransition().getContext());
        }

        public Set<String> confirmedPins() {
            return data().getConfirmedPins();
        }

        private TelcoRechargeData data() {
            return getTransition().getContext().mostRecentOf(TelcoRechargeData.class).get();
        }
    }

    static class InitialView extends StateMachineView {
        protected InitialView() {
            super("initial.mustache");
        }
    }

    static class SuccessView extends StateMachineView {
        protected SuccessView() {
            super("success.mustache");
        }
    }

    static class ErrorView extends StateMachineView {
        protected ErrorView() {
            super("error.mustache");
        }
    }
}
