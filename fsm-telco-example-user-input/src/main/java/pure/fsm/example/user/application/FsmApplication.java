package pure.fsm.example.user.application;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import pure.fsm.example.user.application.api.UserActionResource;
import pure.fsm.example.user.domain.TelcoStateMachine;
import pure.fsm.example.user.domain.event.TimeoutTickEvent;
import pure.fsm.example.user.infra.TelcoGateway;

import java.time.temporal.ChronoUnit;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static pure.fsm.core.cleanup.ContextHistoryFormatter.HISTORY_FORMATTER;

public class FsmApplication extends Application<FsmConfiguration> {

    private TelcoStateMachineBundle stateMachineBundle;

    public static void main(final String[] args) throws Exception {
        new FsmApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<FsmConfiguration> bootstrap) {
        stateMachineBundle = new TelcoStateMachineBundle();

        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.addBundle(stateMachineBundle);
    }

    @Override
    public void run(FsmConfiguration configuration, Environment environment) throws Exception {
        final TelcoGateway telcoGateway = new TelcoGateway(stateMachineBundle.getRepository());
        final TelcoStateMachine stateMachine = new TelcoStateMachine(stateMachineBundle.getDistributedResourceFactory(), telcoGateway);
        telcoGateway.stateMachine = stateMachine;

        stateMachineBundle.createCleaner(newArrayList(HISTORY_FORMATTER), 30, SECONDS, 5, ChronoUnit.SECONDS).startScheduler();
        stateMachineBundle.createEventTicker(30, SECONDS, transition ->
                stateMachine.handleEvent(transition, new TimeoutTickEvent())).start();

        UserActionResource resource = new UserActionResource(
                stateMachineBundle.getRepository(),
                stateMachine, stateMachineBundle.viewFactory);

        environment.jersey().register(resource);
    }
}
