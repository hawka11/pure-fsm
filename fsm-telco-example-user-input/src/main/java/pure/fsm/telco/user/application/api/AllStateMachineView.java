package pure.fsm.telco.user.application.api;

import io.dropwizard.views.View;

import java.util.Set;

public class AllStateMachineView extends View {

    private final Set<String> allIds;

    protected AllStateMachineView(Set<String> allIds) {
        super("all.mustache");
        this.allIds = allIds;
    }

    public Set<String> getAllIds() {
        return allIds;
    }
}
