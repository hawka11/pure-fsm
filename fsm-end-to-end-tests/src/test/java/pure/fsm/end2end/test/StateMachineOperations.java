package pure.fsm.end2end.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.core.event.Event;
import pure.fsm.core.repository.StateMachineRepository;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.core.timeout.TimeoutTicker;
import pure.fsm.end2end.state.InitialState;
import pure.fsm.end2end.state.TelcoStateFactory;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.StateFactoryRegistration.registerStateFactory;
import static pure.fsm.core.template.DefaultStateMachineCallable.handleWithTransition;
import static pure.fsm.end2end.TelcoRechargeContext.initialTelcoRecharge;

public class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    public static final int KEEP_AROUND_B4_REMOVING = 1000;

    final StateMachineRepository repository;
    final StateMachineTemplate template;
    final TelcoStateFactory stateFactory;
    final TimeoutTicker timeoutTicker;
    final CleanUpFinalisedStateMachines cleaner;

    public StateMachineOperations(StateMachineRepository repository) {
        this.repository = repository;
        this.template = new StateMachineTemplate(repository, newArrayList());
        this.stateFactory = new TelcoStateFactory();
        this.timeoutTicker = new TimeoutTicker(repository, template, 1, SECONDS);
        this.cleaner = new CleanUpFinalisedStateMachines(repository, newArrayList(), 1, SECONDS, KEEP_AROUND_B4_REMOVING, MILLIS);
    }

    public Transition getStateMachine(String stateMachineId) {
        return repository.get(stateMachineId);
    }

    public void scheduleEventOnThread(String stateMachineId, final Event event) {
        new Thread(() -> template.tryWithLock(stateMachineId,
                handleWithTransition((prevTransition, stateMachine) -> stateMachine.handleEvent(prevTransition, event)))).start();
    }

    public String createStateMachineInInitialState() {
        registerStateFactory(stateFactory);
        return repository.create(
                stateFactory.getStateByClass(InitialState.class), TelcoStateFactory.class, newArrayList(initialTelcoRecharge()));
    }

    public void logCurrentState(String stateMachineId) {
        LOG.info("Ending.... current state for [{}] is: [{}]", stateMachineId,
                getStateMachine(stateMachineId).getState().getClass().getSimpleName());
    }

    public StateMachineRepository getRepository() {
        return repository;
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
