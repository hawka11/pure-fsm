PureFSM
=========

Pure as in pure Java, no xml or gui to configure.

There are more advanced FSM / BPM frameworks out there but sometimes they can be seen as too heavyweight for certain applications.

The intention of this project is a simple FSM, with influences from AKKA FSM.

The main problem that it is trying to solve is to handle technical process flows between one or more systems.

Main objectives:
 - Pure Java
 - Stateless States / Events / State Machine, all except state machine transition / context
 - State machine context is serializable to support distributed state machine processing (i.e. horizontally scalable)
 - A single state machine can only process a single event at a time
 - A State machine that has transitioned into a Final State (either success / failure) cannot be restarted.
 - Each state can have its own timeout
 
Basic concepts that exist are:
 - Event, something has happened, which may contain event data
 - State, handle a particular event resulting in a transition to another state, possible itself.
 - StateMachine, is a stateless object, that orchestrates the lifecycle of event transition after processing an event.
 - Transition, the only stateful object provided by PureFSM. 
   Contains event transition information (including previous transition) and 
   any other data (Context) the user requires between states.
 - Resource, stored within a Transition. Can be used if there are any external resources 
   that you wish to maintain a lock for the lifetime of a single state machine.
   By default, they are unlocked in the 'onEnter' call of a final state.

Initial Example: https://github.com/hawka11/PureFSM/blob/master/fsm-telco-example-inmemory/src/main/java/pure/fsm/example/inmemory/MainSuccessful.java

