package pure.fsm.end2end.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.EventTicker;
import pure.fsm.core.Transition;
import pure.fsm.core.TransitionRepository;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.end2end.TelcoStateMachine;
import pure.fsm.java.test.fixture.event.TelcoEvent;
import pure.fsm.java.test.fixture.event.TimeoutTickEvent;
import pure.fsm.java.test.fixture.guard.AllPinsRechargedAcceptedGuard;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.EventTicker.defaultAlwaysTicker;
import static pure.fsm.core.WithinLock.tryWithLock;
import static pure.fsm.java.test.fixture.TelcoRechargeContext.initialTelcoRecharge;
import static pure.fsm.java.test.fixture.state.InitialState.INITIAL_STATE;

public class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    public static final int KEEP_AROUND_B4_REMOVING = 1000;

    public final TelcoStateMachine stateMachine = new TelcoStateMachine(new AllPinsRechargedAcceptedGuard());
    public final TransitionRepository repository;
    public final EventTicker timeoutEventTicker;
    public final CleanUpFinalisedStateMachines cleaner;

    public StateMachineOperations(TransitionRepository repository) {
        this.repository = repository;
        this.cleaner = new CleanUpFinalisedStateMachines(repository, newArrayList(), 1, SECONDS, KEEP_AROUND_B4_REMOVING, MILLIS);
        this.timeoutEventTicker = defaultAlwaysTicker(repository, 1, SECONDS, t -> stateMachine.handleEvent(t, new TimeoutTickEvent()));
    }

    public Transition getStateMachine(String stateMachineId) {
        return repository.get(stateMachineId);
    }

    public void scheduleEventOnThread(String stateMachineId, TelcoEvent event) {

        new Thread(() -> tryWithLock(stateMachineId, repository, (last) -> stateMachine.handleEvent(last, event))).start();
    }

    public String createStateMachineInInitialState() {
        return repository.create(INITIAL_STATE, newArrayList(initialTelcoRecharge()));
    }

    public void logCurrentState(String stateMachineId) {
        LOG.info("Ending.... current state for [{}] is: [{}]", stateMachineId,
                getStateMachine(stateMachineId).getState().getClass().getSimpleName());
    }

}
