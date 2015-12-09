package pure.fsm.end2end.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.EventTicker;
import pure.fsm.core.StateMachineRepository;
import pure.fsm.core.Transition;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.core.test.fixture.event.TelcoEvent;
import pure.fsm.core.test.fixture.event.TimeoutTickEvent;
import pure.fsm.core.test.fixture.guard.AllPinsRechargedAcceptedGuard;
import pure.fsm.end2end.TelcoStateMachine;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.WithinLock.tryWithLock;
import static pure.fsm.core.context.InitialContext.initialContext;
import static pure.fsm.core.fixture.TestState.INITIAL_STATE;
import static pure.fsm.core.test.fixture.TelcoRechargeContext.initialTelcoRecharge;

public class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    public static final int KEEP_AROUND_B4_REMOVING = 1000;

    public final TelcoStateMachine stateMachine = new TelcoStateMachine(new AllPinsRechargedAcceptedGuard());
    public final StateMachineRepository repository;
    public final EventTicker timeoutEventTicker;
    public final CleanUpFinalisedStateMachines cleaner;

    public StateMachineOperations(StateMachineRepository repository) {
        this.repository = repository;
        this.cleaner = new CleanUpFinalisedStateMachines(repository, newArrayList(), 1, SECONDS, KEEP_AROUND_B4_REMOVING, MILLIS);
        timeoutEventTicker = new EventTicker(repository, 1, SECONDS, last -> {
            final String id = initialContext(last.getContext()).stateMachineId;
            return tryWithLock(id, repository, transition -> stateMachine.handleEvent(transition, new TimeoutTickEvent()));
        });
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
