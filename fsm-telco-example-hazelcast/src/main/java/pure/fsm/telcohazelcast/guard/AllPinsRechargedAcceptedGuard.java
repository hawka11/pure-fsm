package pure.fsm.telcohazelcast.guard;

import pure.fsm.core.Transition;
import pure.fsm.telco.guard.Guard;
import pure.fsm.telcohazelcast.HzTelcoRechargeContext;

import java.util.Set;

import static pure.fsm.core.context.MostRecentTrait.mostRecentOf;
import static pure.fsm.telcohazelcast.HzTelcoRechargeContext.getRequestedPins;

public class AllPinsRechargedAcceptedGuard implements Guard {

    @Override
    public boolean isSatisfied(Transition transition) {
        int requestedPins = getRequestedPins(transition).size();

        //return true if number of accepted pins equal the number of requested pins
        final HzTelcoRechargeContext hzTelcoRechargeTrait = mostRecentOf(transition, HzTelcoRechargeContext.class).get();
        final Set<String> acceptedPins = hzTelcoRechargeTrait.getAcceptedPins();
        return acceptedPins.size() >= requestedPins;
    }
}
