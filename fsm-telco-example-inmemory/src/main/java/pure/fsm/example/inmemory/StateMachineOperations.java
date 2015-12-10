package pure.fsm.example.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.EventTicker;
import pure.fsm.core.TransitionRepository;
import pure.fsm.core.Transition;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.java.test.fixture.event.TelcoEvent;
import pure.fsm.java.test.fixture.event.TimeoutTickEvent;
import pure.fsm.java.test.fixture.guard.AllPinsRechargedAcceptedGuard;
import pure.fsm.repository.inmemory.InMemoryTransitionRepository;

import java.time.temporal.ChronoUnit;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.WithinLock.tryWithLock;
import static pure.fsm.core.context.InitialContext.initialContext;
import static pure.fsm.java.test.fixture.state.InitialState.INITIAL_STATE;
import static pure.fsm.java.test.fixture.TelcoRechargeContext.initialTelcoRecharge;

class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    final public TransitionRepository repository = new InMemoryTransitionRepository();

    final public CleanUpFinalisedStateMachines cleaner = new CleanUpFinalisedStateMachines(repository, newArrayList(), 5, SECONDS, 5, ChronoUnit.SECONDS);

    final public TelcoStateMachine stateMachine = new TelcoStateMachine(new AllPinsRechargedAcceptedGuard());

    final public EventTicker timeoutEventTicker = new EventTicker(repository, 1, SECONDS, last -> {
        final String id = initialContext(last.getContext()).stateMachineId;
        return tryWithLock(id, repository, transition -> stateMachine.handleEvent(transition, new TimeoutTickEvent()));
    });

    public Transition getStateMachine(String stateMachineId) {
        return repository.get(stateMachineId);
    }

    public void scheduleEventOnThread(String stateMachineId, final TelcoEvent event) {
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
