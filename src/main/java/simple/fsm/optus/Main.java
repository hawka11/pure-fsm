package simple.fsm.optus;

import simple.fsm.core.BaseStateMachineCallback;
import simple.fsm.core.StateMachine;
import simple.fsm.core.StateMachineTemplate;
import simple.fsm.core.accessor.InMemoryStateMachineAccessor;
import simple.fsm.core.accessor.StateMachineAccessor;
import simple.fsm.optus.event.RequestRechargeEvent;
import simple.fsm.optus.state.InitialState;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        final StateMachineAccessor accessor = new InMemoryStateMachineAccessor();
        final StateMachineTemplate template = new StateMachineTemplate(accessor);

        String stateMachineId = accessor.create(
                new InitialState(),
                new OptusRechargeContext(new BigDecimal("20.00")));

        template.tryInLock(stateMachineId, new BaseStateMachineCallback() {
            @Override
            public void doWith(StateMachine stateMachine) {

                RequestRechargeEvent requestRechargeEvent = new RequestRechargeEvent();

                stateMachine.handleEvent(requestRechargeEvent);
            }
        });
    }
}
