package pure.fsm.java.test.fixture.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.unlock.CanUnlock;

import java.util.Set;

public class LockedPinResource implements CanUnlock {

    private final Set<String> pinsToLock;

    @JsonCreator
    public LockedPinResource(@JsonProperty("pinsToLock") Set<String> pinsToLock) {
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
