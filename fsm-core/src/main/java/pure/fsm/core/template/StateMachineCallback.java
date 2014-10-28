package pure.fsm.core.template;

import pure.fsm.core.Context;
import pure.fsm.core.StateMachine;

public interface StateMachineCallback {

    Context doWith(Context context, StateMachine stateMachine);

    void onLockFailed(Exception e);

    Context onError(Context context, StateMachine stateMachine, Exception e);
}
