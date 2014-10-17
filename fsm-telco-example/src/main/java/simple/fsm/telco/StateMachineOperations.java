package simple.fsm.telco;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.StateMachine;
import simple.fsm.core.accessor.CleanUpFinalisedStateMachines;
import simple.fsm.core.accessor.InMemoryStateMachineAccessor;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.core.event.Event;
import simple.fsm.core.template.BaseStateMachineCallback;
import simple.fsm.core.template.StateMachineTemplate;
import simple.fsm.core.timeout.TimeoutTicker;
import simple.fsm.telco.state.InitialState;
import simple.fsm.telco.state.TelcoStateFactory;

import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    final StateMachineAccessor accessor = new InMemoryStateMachineAccessor();
    final StateMachineTemplate template = new StateMachineTemplate(accessor);
    final TelcoStateFactory stateFactory = new TelcoStateFactory();
    final TimeoutTicker timeoutTicker = new TimeoutTicker(accessor, template, 1, TimeUnit.SECONDS);
    final CleanUpFinalisedStateMachines cleaner = new CleanUpFinalisedStateMachines(accessor, 5, SECONDS);

    public StateMachine getStateMachine(String stateMachineId) {
        return accessor.get(stateMachineId);
    }

    public void scheduleEventOnThread(String stateMachineId, final Event event) {
        new Thread(() -> template.tryWithLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public StateMachine doWith(StateMachine stateMachine) {
                return stateMachine.handleEvent(event);
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

    public StateMachineAccessor getAccessor() {
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
