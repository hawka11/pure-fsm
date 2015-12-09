package pure.fsm.example.inmemory.event;

import pure.fsm.core.Transition;

public interface TelcoEvent {
    Transition accept(Transition last, TelcoEventVisitor visitor);
}
