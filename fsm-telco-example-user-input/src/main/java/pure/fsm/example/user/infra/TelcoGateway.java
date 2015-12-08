package pure.fsm.example.user.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.WithinLock;
import pure.fsm.example.user.domain.event.RequestAcceptedEvent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.template.DefaultStateMachineCallable.handleWithTransition;

public class TelcoGateway {

    private static final boolean SUCCESS = true;
    private static final Logger LOG = LoggerFactory.getLogger(TelcoGateway.class);

    private final ScheduledExecutorService service;
    private final WithinLock template;

    public TelcoGateway(WithinLock template) {
        this.template = template;
        service = Executors.newSingleThreadScheduledExecutor();
    }

    public boolean requestPinRecharge(String smId, List<String> pins) {
        service.schedule(() -> template.tryWithLock(smId, handleWithTransition((prevTransition, stateMachine) -> {
            LOG.info("about to accept request of pins [{}]", pins);
            return stateMachine.handleEvent(prevTransition, new RequestAcceptedEvent(pins));
        })), 1, SECONDS);

        return SUCCESS;
    }
}
