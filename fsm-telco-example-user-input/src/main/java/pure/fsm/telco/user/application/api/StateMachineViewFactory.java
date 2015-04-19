package pure.fsm.telco.user.application.api;

import io.dropwizard.views.View;
import pure.fsm.core.Transition;
import pure.fsm.core.state.ErrorFinalState;
import pure.fsm.core.state.State;
import pure.fsm.core.state.SuccessFinalState;
import pure.fsm.core.state.TimedOutFinalState;
import pure.fsm.telco.user.domain.TelcoRechargeData;
import pure.fsm.telco.user.domain.state.InitialState;
import pure.fsm.telco.user.domain.state.WaitingForConfirmationState;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static pure.fsm.core.context.MostRecentContext.mostRecentOf;

public class StateMachineViewFactory {

    private final static Map<Class<? extends State>, Class<? extends StateMachineView>> viewByStateMachineState;

    static {
        viewByStateMachineState = newHashMap();

        viewByStateMachineState.put(InitialState.class, InitialView.class);
        viewByStateMachineState.put(WaitingForConfirmationState.class, WatingView.class);
        viewByStateMachineState.put(SuccessFinalState.class, SuccessView.class);

        viewByStateMachineState.put(ErrorFinalState.class, ErrorView.class);
        viewByStateMachineState.put(TimedOutFinalState.class, ErrorView.class);
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
    }

    static class WatingView extends StateMachineView {
        protected WatingView() {
            super("waiting.mustache");
        }

        public Set<String> nonConfirmedPins() {
            return data().nonConfirmedPins(getTransition());
        }

        public Set<String> confirmedPins() {
            return data().getConfirmedPins();
        }

        private TelcoRechargeData data() {
            return mostRecentOf(getTransition(), TelcoRechargeData.class).get();
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
