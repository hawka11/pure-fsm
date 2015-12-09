package pure.fsm.example.user.application.api;

import io.dropwizard.views.View;
import pure.fsm.core.StateMachineRepository;
import pure.fsm.core.Transition;
import pure.fsm.example.user.domain.TelcoStateMachine;
import pure.fsm.example.user.domain.event.ConfirmPinEvent;
import pure.fsm.example.user.domain.event.RequestPinEvent;

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
import static pure.fsm.core.WithinLock.tryWithLock;
import static pure.fsm.example.user.domain.TelcoRechargeData.initialTelcoRechargeData;
import static pure.fsm.example.user.domain.state.InitialState.INITIAL_STATE;

@Path("/sm")
public class UserActionResource {

    private final StateMachineRepository repository;
    private final TelcoStateMachine stateMachine;
    private final StateMachineViewFactory viewFactory;

    public UserActionResource(StateMachineRepository repository,
                              TelcoStateMachine stateMachine,
                              StateMachineViewFactory viewFactory) {
        this.repository = repository;
        this.stateMachine = stateMachine;
        this.viewFactory = viewFactory;
    }

    @GET
    public View getAll() {
        Set<String> allIds = repository.getIds();
        return new AllStateMachineView(allIds);
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public View create() {
        repository.create(INITIAL_STATE, newArrayList(initialTelcoRechargeData()));
        return getAll();
    }

    @POST
    @Path("/{id}/pin/request")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public View requestPins(@PathParam("id") String id,
                            @FormParam("pin") Set<String> pins) {

        tryWithLock(id, repository, (last) -> {
                    List<String> nonEmptyPins = pins.stream().filter(p -> p.length() > 0).collect(toList());
                    return stateMachine.handleEvent(last, new RequestPinEvent(nonEmptyPins));
                }
        );

        return getStateBasedView(id);
    }

    @POST
    @Path("/{id}/pin/{pin}/confirm")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public View confirmPin(@PathParam("id") String id,
                           @PathParam("pin") String pin) {

        tryWithLock(id, repository, (last) -> stateMachine.handleEvent(last, new ConfirmPinEvent(pin)));

        return getStateBasedView(id);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    public View getStateBasedView(@PathParam("id") String id) {
        Transition transition = repository.get(id);
        Optional<View> maybeView = viewFactory.getViewFor(transition);

        return maybeView.orElseThrow(() -> new WebServiceException("no views configured"));
    }
}
