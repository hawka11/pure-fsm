package pure.fsm.telcohazelcast.guard;

import pure.fsm.core.Context;
import pure.fsm.telco.guard.Guard;
import pure.fsm.telcohazelcast.HzTelcoRechargeContext;

import java.util.Set;

import static pure.fsm.telcohazelcast.HzTelcoRechargeContext.getRequestedPins;


public class AllPinsRechargedAcceptedGuard implements Guard {

    @Override
    public boolean isSatisfied(Context context) {
        int requestedPins = getRequestedPins(context).size();

        //return true if number of accepted pins equal the number of requested pins
        final HzTelcoRechargeContext rechargeContext = context.mostRecentOf(HzTelcoRechargeContext.class).get();
        final Set<String> acceptedPins = rechargeContext.getAcceptedPins();
        return acceptedPins.size() >= requestedPins;
    }
}
