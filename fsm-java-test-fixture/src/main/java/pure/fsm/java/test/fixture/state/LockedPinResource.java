package pure.fsm.java.test.fixture.state;

import pure.fsm.core.unlock.CanUnlock;

import java.util.Set;

public class LockedPinResource implements CanUnlock {

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
