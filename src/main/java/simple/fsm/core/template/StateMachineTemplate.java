package simple.fsm.core.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.StateMachine;
import simple.fsm.core.accessor.StateMachineAccessor;

public class StateMachineTemplate {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineTemplate.class);

    private final StateMachineAccessor stateMachineAccessor;

    public StateMachineTemplate(StateMachineAccessor stateMachineAccessor) {
        this.stateMachineAccessor = stateMachineAccessor;
    }

    /**
     * We only want one thread/event to be processed at a time for a given state machine,
     * this method should be used to synchronise any event handling within a single state machine.
     * <p>
     * This does not prevent multiple state machines being sent their own events concurrently
     */
    public void tryWithLock(String stateMachineId, StateMachineCallback stateMachineCallback) {
        StateMachine currentStateMachine = null;
        try {
            currentStateMachine = stateMachineAccessor.tryLock(stateMachineId);
        } catch (Exception e) {
            LOG.error("Error with currentStateMachine [{}]", stateMachineId);
            stateMachineCallback.lockFailed(e);
        }

        if (currentStateMachine != null) {
            try {
                StateMachine newStateMachine = stateMachineCallback.doWith(currentStateMachine);
                stateMachineAccessor.update(stateMachineId, newStateMachine);
            } catch (Exception e) {
                LOG.error("Error with currentStateMachine [{}]", stateMachineId);
                StateMachine newStateMachine = stateMachineCallback.onError(currentStateMachine, e);
                stateMachineAccessor.update(stateMachineId, newStateMachine);
            } finally {
                stateMachineAccessor.unlock(stateMachineId);
            }
        }
    }
}
