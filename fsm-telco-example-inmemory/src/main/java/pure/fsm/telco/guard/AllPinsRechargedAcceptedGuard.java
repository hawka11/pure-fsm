package pure.fsm.telco.guard;

import pure.fsm.core.Context;
import pure.fsm.telco.TelcoRechargeTrait;

import static pure.fsm.core.context.MostRecentTrait.mostRecentOf;
import static pure.fsm.telco.TelcoRechargeTrait.getRequestedPins;

public class AllPinsRechargedAcceptedGuard implements Guard {

    @Override
    public boolean isSatisfied(Context context) {
        int requestedPins = getRequestedPins(context).size();

        //return true if number of accepted pins equal the number of requested pins
        return mostRecentOf(context, TelcoRechargeTrait.class).get().getAcceptedPins().size() == requestedPins;
    }
}
