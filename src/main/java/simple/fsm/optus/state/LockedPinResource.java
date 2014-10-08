package simple.fsm.optus.state;

import simple.fsm.core.Resource;

public class LockedPinResource implements Resource {

    @Override
    public void unlock() {
        System.out.println("unlocking LockedPinResource");
    }
}
