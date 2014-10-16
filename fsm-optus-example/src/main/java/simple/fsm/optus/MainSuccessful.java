package simple.fsm.optus;

import simple.fsm.optus.event.RechargeAcceptedEvent;
import simple.fsm.optus.event.RequestRechargeEvent;

import java.math.BigDecimal;

public class MainSuccessful {

    public static void main(String[] args) throws Exception {
        StateMachineOperations operations = new StateMachineOperations();

        //create state machine
        final String stateMachineId = operations.createStateMachineInInitialState();

        //One thread will send RequestRechargeEvent to sm
        operations.scheduleEventOnThread(stateMachineId, new RequestRechargeEvent(new BigDecimal("20.00")));
        Thread.sleep(2000);

        //Sometime later, another thread will send RechargeAcceptedEvent to sm
        //Probably invoked by a callback via optus webservice
        operations.scheduleEventOnThread(stateMachineId, new RechargeAcceptedEvent());
        Thread.sleep(2000);

        //This 'current' state could be inspected by anything, which could react as desired / or send their own event to sm etc...
        System.out.println("Ending.... current state is: " +
                operations.getStateMachine(stateMachineId).getCurrentState().getClass().getSimpleName());
    }
}
