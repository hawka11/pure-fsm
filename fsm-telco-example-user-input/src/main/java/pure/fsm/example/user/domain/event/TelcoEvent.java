package pure.fsm.example.user.domain.event;

import pure.fsm.core.Transition;

public interface TelcoEvent {
    Transition accept(Transition last, TelcoEventVisitor visitor);
}
