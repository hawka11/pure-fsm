package pure.fsm.telco.user.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;
import pure.fsm.core.template.BaseStateMachineCallback;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.telco.user.domain.event.RequestAcceptedEvent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TelcoGateway {

    private static final boolean SUCCESS = true;
    private static final Logger LOG = LoggerFactory.getLogger(TelcoGateway.class);

    private final ScheduledExecutorService service;
    private final StateMachineTemplate template;

    public TelcoGateway(StateMachineTemplate template) {
        this.template = template;
        service = Executors.newSingleThreadScheduledExecutor();
    }

    public boolean requestPinRecharge(String smId, List<String> pins) {
        service.schedule(() -> template.tryWithLock(smId, new BaseStateMachineCallback() {
            @Override
            public Context doWith(Context context, StateMachine stateMachine) {
                LOG.info("about to accept request of pins [{}]", pins);
                return stateMachine.handleEvent(context, new RequestAcceptedEvent(pins));
            }
        }), 1, SECONDS);

        return SUCCESS;
    }
}
