package pure.fsm.core.transition;

import pure.fsm.core.Context;
import pure.fsm.core.Transition;
import pure.fsm.core.event.Event;

import static pure.fsm.core.context.ContextMessage.withMessage;

public class UserCancelled {

    public static Transition transitionToUserCancelled(Context context, Event event) {
        final Context updatedContext = context.appendState(withMessage("USER_CANCELED"));

        return Transition.To(updatedContext.stateFactory().userCanceled(), event, updatedContext);
    }
}
