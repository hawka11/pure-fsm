package simple.fsm.optus;

import simple.fsm.core.template.BaseStateMachineCallback;
import simple.fsm.core.StateMachine;
import simple.fsm.core.template.StateMachineTemplate;
import simple.fsm.core.accessor.InMemoryStateMachineAccessor;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.optus.event.RechargeAcceptedEvent;
import simple.fsm.optus.event.RequestRechargeEvent;
import simple.fsm.optus.state.InitialState;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) throws Exception {
        final StateMachineAccessor accessor = new InMemoryStateMachineAccessor();
        final StateMachineTemplate template = new StateMachineTemplate(accessor);

        final String stateMachineId = accessor.create(
                new InitialState(),
                new OptusRechargeContext());

        new Thread(() -> template.tryWithLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public void doWith(StateMachine stateMachine) {
                RequestRechargeEvent event = new RequestRechargeEvent(new BigDecimal("20.00"));
                stateMachine.handleEvent(event);
            }
        })).run();

        Thread.sleep(2000);

        new Thread(() -> template.tryWithLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public void doWith(StateMachine stateMachine) {
                RechargeAcceptedEvent event = new RechargeAcceptedEvent();
                stateMachine.handleEvent(event);
            }
        })).run();

        Thread.sleep(2000);

        System.out.println("Ending.... current state is: " + accessor.getSnapshot(stateMachineId).getCurrentState().getClass().getSimpleName());
    }
}
