package pure.fsm.end2end.guard;

import pure.fsm.core.Context;
import pure.fsm.end2end.TelcoRechargeContext;

import static pure.fsm.end2end.TelcoRechargeContext.getRequestedPins;

public class AllPinsRechargedAcceptedGuard implements Guard {

    @Override
    public boolean isSatisfied(Context context) {
        int requestedPins = getRequestedPins(context).size();

        //return true if number of accepted pins equal the number of requested pins
        final TelcoRechargeContext rechargeContext = context.mostRecentOf(TelcoRechargeContext.class).get();
        return rechargeContext.getAcceptedPins().size() == requestedPins;
    }
}
