package pure.fsm.example.user.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.StateMachineRepository;
import pure.fsm.example.user.domain.TelcoStateMachine;
import pure.fsm.example.user.domain.event.RequestAcceptedEvent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.WithinLock.tryWithLock;

public class TelcoGateway {

    private static final boolean SUCCESS = true;
    private static final Logger LOG = LoggerFactory.getLogger(TelcoGateway.class);

    public TelcoStateMachine stateMachine;

    private final ScheduledExecutorService service;
    private final StateMachineRepository repository;

    public TelcoGateway(StateMachineRepository repository) {
        this.repository = repository;
        service = Executors.newSingleThreadScheduledExecutor();
    }

    public boolean requestPinRecharge(String smId, List<String> pins) {
        service.schedule(() -> tryWithLock(smId, repository, (last -> {
            LOG.info("about to accept request of pins [{}]", pins);
            return stateMachine.handleEvent(last, new RequestAcceptedEvent(pins));
        })), 1, SECONDS);

        return SUCCESS;
    }
}
