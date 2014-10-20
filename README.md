PureFSM --> TODO: rename github name
=========

Pure as in pure Java, no xml or gui to configure.

There are more advanced FSM / BPM frameworks out there but sometimes they can be seen as too heavyweight for certain applications.

The intention of this project is more a simplistic FSM, with influences from AKKA FSM and past experience.

The intentions are:
 - Pure Java
 - Stateless States / Events etc... (except state machine and state machine context)
 - State machine context is serializable to support distributed state machine processing (i.e. horizaontally scalable)
 - A single state machine can only process a single event at a time
 - A State machine that has transitioned into a Final State (either success / failure) cannot be restarted.
 - Each state can have its own timeout
 
Basic concepts that exist are:
 - Event, something has happened, inform current state about event which may contain event data.
 - State, do something when event 'e' arrives
 - StateMachine, *should* be the only stateful object. holds current state, context, previous state etc...
 - Context, *should* be the only stateful object. Holds any data required between 2 or more states
 - Resource, held within the context. Can be used if there are any external resources that you wish to maintain a lock whilst the state machine is active, until they are unlocked in the 'onEnter' call of a final state.

TODO: write tests.
