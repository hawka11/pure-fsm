package pure.fsm.telcohazelcast.guard;

import pure.fsm.core.Context;
import pure.fsm.telco.guard.Guard;
import pure.fsm.telcohazelcast.HzTelcoRechargeTrait;

import java.util.Set;

import static pure.fsm.core.context.MostRecentTrait.mostRecentOf;
import static pure.fsm.telcohazelcast.HzTelcoRechargeTrait.getRequestedPins;

public class AllPinsRechargedAcceptedGuard implements Guard {

    @Override
    public boolean isSatisfied(Context context) {
        int requestedPins = getRequestedPins(context).size();

        //return true if number of accepted pins equal the number of requested pins
        final HzTelcoRechargeTrait hzTelcoRechargeTrait = mostRecentOf(context, HzTelcoRechargeTrait.class).get();
        final Set<String> acceptedPins = hzTelcoRechargeTrait.getAcceptedPins();
        return acceptedPins.size() >= requestedPins;
    }
}
