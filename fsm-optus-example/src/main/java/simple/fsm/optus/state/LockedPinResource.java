package simple.fsm.optus.state;

import simple.fsm.core.Resource;

public class LockedPinResource implements Resource {

    private String pin = "4444";

    public String getPin() {
        return pin;
    }

    @Override
    public void unlock() {
        System.out.println("unlocking LockedPinResource");
    }
}
