package pure.fsm.core.template;

import pure.fsm.core.StateMachine;
import pure.fsm.core.Transition;

import java.util.Optional;

public interface StateMachineCallback {

    Optional<Transition> doWith(Transition prevTransition, StateMachine stateMachine);

    void onLockFailed(Exception e);

    Transition onError(Transition prevTransition, StateMachine stateMachine, Exception e);
}
