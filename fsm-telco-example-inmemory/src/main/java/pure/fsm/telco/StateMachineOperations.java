package pure.fsm.telco;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;
import pure.fsm.core.accessor.CleanUpFinalisedStateMachines;
import pure.fsm.core.accessor.StateMachineContextAccessor;
import pure.fsm.core.event.Event;
import pure.fsm.core.template.BaseStateMachineCallback;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.core.timeout.TimeoutTicker;
import pure.fsm.inmemory.accessor.InMemoryStateMachineContextAccessor;
import pure.fsm.telco.state.InitialState;
import pure.fsm.telco.state.TelcoStateFactory;

import java.time.temporal.ChronoUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    final StateMachineContextAccessor accessor = new InMemoryStateMachineContextAccessor();
    final StateMachineTemplate template = new StateMachineTemplate(accessor);
    final TelcoStateFactory stateFactory = new TelcoStateFactory();
    final TimeoutTicker timeoutTicker = new TimeoutTicker(accessor, template, 1, SECONDS);
    final CleanUpFinalisedStateMachines cleaner = new CleanUpFinalisedStateMachines(accessor, 5, SECONDS, 5, ChronoUnit.SECONDS);

    public Context getStateMachine(String stateMachineId) {
        return accessor.get(stateMachineId);
    }

    public void scheduleEventOnThread(String stateMachineId, final Event event) {
        new Thread(() -> template.tryWithLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public Context doWith(Context context, StateMachine stateMachine) {
                return stateMachine.handleEvent(context, event);
            }
        })).start();
    }

    public String createStateMachineInInitialState() {
        return accessor.create(
                stateFactory.getStateByClass(InitialState.class),
                new TelcoRechargeContext());
    }

    public void logCurrentState(String stateMachineId) {
        LOG.info("Ending.... current state for [{}] is: [{}]", stateMachineId,
                getStateMachine(stateMachineId).getCurrentState().getClass().getSimpleName());
    }

    public StateMachineContextAccessor getAccessor() {
        return accessor;
    }

    public StateMachineTemplate getTemplate() {
        return template;
    }

    public TelcoStateFactory getStateFactory() {
        return stateFactory;
    }

    public TimeoutTicker getTimeoutTicker() {
        return timeoutTicker;
    }

    public CleanUpFinalisedStateMachines getCleaner() {
        return cleaner;
    }
}
