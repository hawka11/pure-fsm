package pure.fsm.core.test.fixture.guard;

import pure.fsm.core.Context;
import pure.fsm.core.test.fixture.TelcoRechargeContext;

public class AllPinsRechargedAcceptedGuard implements Guard {

    @Override
    public boolean isSatisfied(Context context) {
        int requestedPins = TelcoRechargeContext.getRequestedPins(context).size();

        //return true if number of accepted pins equal the number of requested pins
        final TelcoRechargeContext rechargeContext = context.mostRecentOf(TelcoRechargeContext.class).get();
        return rechargeContext.getAcceptedPins().size() == requestedPins;
    }
}
