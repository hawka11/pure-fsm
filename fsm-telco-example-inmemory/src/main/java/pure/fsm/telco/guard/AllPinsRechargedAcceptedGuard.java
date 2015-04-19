package pure.fsm.telco.guard;

import pure.fsm.core.Transition;
import pure.fsm.telco.TelcoRechargeContext;

import static pure.fsm.core.context.MostRecentContext.mostRecentOf;
import static pure.fsm.telco.TelcoRechargeContext.getRequestedPins;

public class AllPinsRechargedAcceptedGuard implements Guard {

    @Override
    public boolean isSatisfied(Transition transition) {
        int requestedPins = getRequestedPins(transition).size();

        //return true if number of accepted pins equal the number of requested pins
        final TelcoRechargeContext context = mostRecentOf(transition, TelcoRechargeContext.class).get();
        return context.getAcceptedPins().size() == requestedPins;
    }
}
