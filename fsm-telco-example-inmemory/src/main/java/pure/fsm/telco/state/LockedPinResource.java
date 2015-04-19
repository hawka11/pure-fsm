package pure.fsm.telco.state;

import pure.fsm.core.context.CanUnlockContext;

import java.util.Set;

public class LockedPinResource implements CanUnlockContext {

    private final Set<String> pinsToLock;

    public LockedPinResource(Set<String> pinsToLock) {

        this.pinsToLock = pinsToLock;
    }

    public Set<String> getPinsToLock() {
        return pinsToLock;
    }

    @Override
    public void unlock() {
        System.out.println("unlocking LockedPinResource");
    }
}
