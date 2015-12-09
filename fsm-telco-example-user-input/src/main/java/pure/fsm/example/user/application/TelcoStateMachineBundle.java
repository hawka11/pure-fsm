package pure.fsm.example.user.application;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import pure.fsm.dropwizard.hazelcast.bundle.StateMachineBundle;
import pure.fsm.example.user.application.api.StateMachineViewFactory;

public class TelcoStateMachineBundle extends StateMachineBundle {

    public StateMachineViewFactory viewFactory;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(Environment environment) {
        super.run(environment);
        viewFactory = new StateMachineViewFactory();
    }
}
