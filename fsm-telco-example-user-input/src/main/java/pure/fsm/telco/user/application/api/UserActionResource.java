package pure.fsm.telco.user.application.api;

import io.dropwizard.views.View;
import pure.fsm.core.Transition;
import pure.fsm.core.StateMachine;
import pure.fsm.core.template.BaseStateMachineCallback;
import pure.fsm.core.template.StateMachineTemplate;
import pure.fsm.telco.user.domain.event.ConfirmPinEvent;
import pure.fsm.telco.user.domain.event.RequestPinEvent;
import pure.fsm.telco.user.domain.state.InitialState;
import pure.fsm.telco.user.domain.state.TelcoStateFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.WebServiceException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static pure.fsm.telco.user.domain.TelcoRechargeData.initialTelcoRechargeData;

@Path("/sm")
public class UserActionResource {

    private final StateMachineTemplate template;
    private final StateMachineViewFactory viewFactory;
    private final TelcoStateFactory stateFactory;

    public UserActionResource(StateMachineTemplate template,
                              StateMachineViewFactory viewFactory,
                              TelcoStateFactory stateFactory) {
        this.template = template;
        this.viewFactory = viewFactory;
        this.stateFactory = stateFactory;
    }

    @GET
    public View getAll() {
        Set<String> allIds = template.getAllIds();
        return new AllStateMachineView(allIds);
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public View create() {
        template.create(stateFactory.getStateByClass(InitialState.class), TelcoStateFactory.class, newArrayList(initialTelcoRechargeData()));
        return getAll();
    }

    @POST
    @Path("/{id}/pin/request")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public View requestPins(@PathParam("id") String id,
                            @FormParam("pin") Set<String> pins) {

        template.tryWithLock(id, new BaseStateMachineCallback() {
            @Override
            public Transition doWith(Transition transition, StateMachine stateMachine) {
                List<String> nonEmptyPins = pins.stream().filter(p -> p.length() > 0).collect(toList());

                return stateMachine.handleEvent(transition, new RequestPinEvent(nonEmptyPins));
            }
        });

        return getStateBasedView(id);
    }

    @POST
    @Path("/{id}/pin/{pin}/confirm")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public View confirmPin(@PathParam("id") String id,
                           @PathParam("pin") String pin) {

        template.tryWithLock(id, new BaseStateMachineCallback() {
            @Override
            public Transition doWith(Transition transition, StateMachine stateMachine) {

                return stateMachine.handleEvent(transition, new ConfirmPinEvent(pin));
            }
        });

        return getStateBasedView(id);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    public View getStateBasedView(@PathParam("id") String id) {
        Transition transition = template.get(id);
        Optional<View> maybeView = viewFactory.getViewFor(transition);

        return maybeView.orElseThrow(() -> new WebServiceException("no views configured"));
    }
}
