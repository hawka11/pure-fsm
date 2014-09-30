package simple.fsm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.fsm.core.accessor.StateMachineAccessor;

public class StateMachineTemplate {

    private final static Logger LOG = LoggerFactory.getLogger(StateMachineTemplate.class);

    private final StateMachineAccessor stateMachineAccessor;

    public StateMachineTemplate(StateMachineAccessor stateMachineAccessor) {
        this.stateMachineAccessor = stateMachineAccessor;
    }

    public void tryInLock(String stateMachineId, StateMachineCallback stateMachineCallback) {
        StateMachine sm = null;
        try {
            sm = stateMachineAccessor.tryLock(stateMachineId);
            stateMachineCallback.doWith(sm);
        } catch (Exception e) {
            LOG.error("Error with sm [{}]", stateMachineId);
            stateMachineCallback.onError(e);
        } finally {
            if (sm != null) {
                stateMachineAccessor.unlock(stateMachineId);
            }
        }
    }
}
