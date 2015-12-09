package pure.fsm.java.test.fixture.event;

import pure.fsm.core.Transition;

public interface TelcoEvent {
    Transition accept(Transition last, TelcoEventVisitor visitor);
}
