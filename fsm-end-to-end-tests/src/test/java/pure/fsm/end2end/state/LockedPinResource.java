package pure.fsm.end2end.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pure.fsm.core.context.CanUnlock;

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
