package pure.fsm.example.inmemory;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Transition;
import pure.fsm.core.cleanup.CleanUpFinalisedStateMachines;
import pure.fsm.core.StateMachineRepository;
import pure.fsm.core.event.Event;
import pure.fsm.core.WithinLock;
import pure.fsm.core.timeout.EventTicker;
import pure.fsm.repository.inmemory.InMemoryStateMachineRepository;
import pure.fsm.example.inmemory.state.InitialState;
import pure.fsm.example.inmemory.state.TelcoStateFactory;

import java.time.temporal.ChronoUnit;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.StateFactoryRegistration.registerStateFactory;
import static pure.fsm.core.template.DefaultStateMachineCallable.handleWithTransition;

class StateMachineOperations {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineOperations.class);

    final StateMachineRepository repository = new InMemoryStateMachineRepository();
    final WithinLock template = new WithinLock(repository, newArrayList());
    final TelcoStateFactory stateFactory = new TelcoStateFactory();
    final EventTicker eventTicker = new EventTicker(repository, template, handleEvent, 1, SECONDS);
    final CleanUpFinalisedStateMachines cleaner = new CleanUpFinalisedStateMachines(repository, newArrayList(), 5, SECONDS, 5, ChronoUnit.SECONDS);

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
                stateFactory.getStateByClass(InitialState.class), TelcoStateFactory.class, Lists.newArrayList(TelcoRechargeContext.initialTelcoRecharge()));
    }

    public void logCurrentState(String stateMachineId) {
        LOG.info("Ending.... current state for [{}] is: [{}]", stateMachineId,
                getStateMachine(stateMachineId).getState().getClass().getSimpleName());
    }

    public StateMachineRepository getRepository() {
        return repository;
    }

    public WithinLock getTemplate() {
        return template;
    }

    public TelcoStateFactory getStateFactory() {
        return stateFactory;
    }

    public EventTicker getEventTicker() {
        return eventTicker;
    }

    public CleanUpFinalisedStateMachines getCleaner() {
        return cleaner;
    }
}
