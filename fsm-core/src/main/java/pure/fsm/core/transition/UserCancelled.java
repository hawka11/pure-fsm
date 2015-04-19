package pure.fsm.core.transition;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static pure.fsm.core.context.MessageContext.withMessage;

public class UserCancelled {

    public static Transition transitionWithUserCancelled(Transition transition, Event event) {
        return transitionWithUserCancelled(transition, event);
    }

    public static Transition transitionWithUserCancelled(Transition transition, Event event, List<Context> contexts) {
        final List<Context> allContexts = newArrayList(contexts);
        allContexts.add(withMessage("USER_CANCELED"));

        return transition.transitionTo(transition.stateFactory().userCanceled(), event, allContexts);
    }
}
