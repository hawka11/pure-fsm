package pure.fsm.telco.user.application;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import pure.fsm.telco.user.application.api.UserActionResource;

import java.time.temporal.ChronoUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FsmApplication extends Application<FsmConfiguration> {

    private TelcoStateMachineBundle stateMachineBundle;

    public static void main(final String[] args) throws Exception {
        new FsmApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<FsmConfiguration> bootstrap) {
        stateMachineBundle = new TelcoStateMachineBundle();

        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(stateMachineBundle);
    }

    @Override
    public void run(FsmConfiguration configuration, Environment environment) throws Exception {
        stateMachineBundle.getTimeoutTicker(10, SECONDS).startTickScheduler();
        stateMachineBundle.getCleaner(10, SECONDS, 10, ChronoUnit.SECONDS).startScheduler();

        UserActionResource resource = new UserActionResource(
                stateMachineBundle.getAccessor(),
                stateMachineBundle.getTemplate(),
                stateMachineBundle.getStateMachineViewFactory(),
                stateMachineBundle.getTelcoStateFactory());

        environment.jersey().register(resource);
    }
}
