package pure.fsm.telco.user.application.api;

import io.dropwizard.views.View;
import pure.fsm.core.Context;
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
import static pure.fsm.core.context.MostRecentTrait.currentState;
import static pure.fsm.core.context.MostRecentTrait.mostRecentOf;

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

    public Optional<View> getViewFor(Context context) {

        try {
            Class<? extends StateMachineView> view = viewByStateMachineState.get(currentState(context).getClass());
            if (view != null) {
                StateMachineView smView = view.newInstance();
                smView.setContext(context);
                return Optional.of(smView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    static abstract class StateMachineView extends View {

        private Context context;

        protected StateMachineView(String templateName) {
            super(templateName);
        }

        void setContext(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }
    }

    static class WatingView extends StateMachineView {
        protected WatingView() {
            super("waiting.mustache");
        }

        public Set<String> nonConfirmedPins() {
            return data().nonConfirmedPins(getContext());
        }

        public Set<String> confirmedPins() {
            return data().getConfirmedPins();
        }

        private TelcoRechargeData data() {
            return mostRecentOf(getContext(), TelcoRechargeData.class).get();
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
