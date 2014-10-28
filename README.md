PureFSM
=========

Pure as in pure Java, no xml or gui to configure.

There are more advanced FSM / BPM frameworks out there but sometimes they can be seen as too heavyweight for certain applications.

The intention of this project is more a simplistic FSM, with influences from AKKA FSM.

The intentions are:
 - Pure Java
 - Stateless States / Events / State Machine, except state machine context
 - State machine context is serializable to support distributed state machine processing (i.e. horizontally scalable)
 - A single state machine can only process a single event at a time
 - A State machine that has transitioned into a Final State (either success / failure) cannot be restarted.
 - Each state can have its own timeout
 
Basic concepts that exist are:
 - Event, something has happened, inform current state about event (event, may contain event data)
 - State, do something when event 'e' arrives
 - StateMachine, is a stateless object, that orchestrates the transitioning of states after processing an event.
 - Context, *should* be the only stateful object. Holds any data required between 2 or more states for the lifetime of one single state machine.
 - Resource, held within the context. Can be used if there are any external resources that you wish to maintain a lock for the lifetime of a single state machine.
 By default, they are unlocked in the 'onEnter' call of a final state.

Initial Example: https://github.com/hawka11/PureFSM/blob/master/fsm-telco-example-inmemory/src/main/java/pure/fsm/telco/MainSuccessful.java

TODO: write tests.
