package pure.fsm.telco.guard;

import pure.fsm.telco.TelcoRechargeContext;

public class AllPinsRechargedAcceptedGuard {

    public boolean isSatisfied(TelcoRechargeContext context) {
        int requestedPins = context.getRequestedPins().size();

        //return true if number of accepted pins equal the number of requested pins
        return context.getAcceptedPins().size() == requestedPins;
    }
}
