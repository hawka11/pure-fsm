PureFSM
=========

Pure as in pure Java, no xml or gui to configure.

There are more advanced FSM / BPM frameworks out there but sometimes they can be seen as too heavyweight for certain applications.

The intention of this project is a lightweight FSM, with influences from AKKA FSM.

The main problem that it is trying to solve is to handle technical process flows between one or more systems.

Main objectives:

 - Pure Java
 - Stateless States / Events / State Machine, all except state machine transition / context
 - State machine context is serializable to support distributed state machine processing (i.e. horizontally scalable)
 - A single state machine can only process a single event at a time
 - Once a State machine has transitioned into a Final State (either success / failure) it cannot be restarted.
 - Each state can have its own timeout
 - After a pre-determined amount of time, any state will be removed. 
 
Basic concepts that exist are:

 - Event, which may contain event data
 - State, handle one or more events resulting in a transition to another state, possible itself.
 - StateMachine, stateless object that orchestrates the lifecycle of event transition after processing an event.
 - Transition, the only stateful object provided by PureFSM. 
    - Contains event transition information (including previous transition) and any other data (Context) the user requires between states.

Pluggable persistent layer (current implementations):  
 
 - InMemory (testing / single node)
 - Distributed Hazelcast
 - Distributed Mysql

Kotlin Example
========

Recharging a mobile phone via telco company

###States

```
public sealed class TelcoState {
    object InitialState : TelcoState()
    object RechargeRequestedState : TelcoState()
    object RechargeConfirmedFinalState : TelcoState(), FinalState
    object TimeoutFinalState : TelcoState(), FinalState
    object ErrorFinalState : TelcoState(), FinalState
}
```

###Events

```
public sealed class TelcoEvent {
    class RechargeEvent(val amount: BigDecimal, val pins: List<String>) : TelcoEvent()
    class RechargeConfirmEvent(val pins: List<String>) : TelcoEvent()
    object TimeoutTick : TelcoEvent()
}
```

###State Machine
```
class TelcoStateMachine : StateMachine<TelcoEvent>() {

    init {

        `when`(InitialState, { last, event ->
            when (event) {
                is RechargeEvent -> /*process, then*/ go(RechargeRequestedState, event, last.context)
                is TimeoutTick -> checkTimeout(last)
                else -> defaultHandle(last, event)
            }
        })

        `when`(RechargeRequestedState, { last, event ->
            when (event) {
                is RechargeConfirmEvent -> go(RechargeConfirmedFinalState, event, last.context)
                is TimeoutTick -> checkTimeout(last)
                else -> defaultHandle(last, event)
            }
        })
    }

    private fun checkTimeout(last: Transition) = if (isTimeout(last)) go(TimeoutFinalState, TimeoutTick, last.context) else stay(last.state, TimeoutTick, last.context)

    private fun isTimeout(last: Transition) = LocalDateTime.now().isAfter(last.transitioned.plusSeconds(TIMEOUT_SECS))

    private fun defaultHandle(last: Transition, event: TelcoEvent) = go(ErrorFinalState, event, last.context)
}
```

Initial Java Example
=======
https://github.com/hawka11/PureFSM/blob/master/fsm-telco-example-inmemory/src/main/java/pure/fsm/example/inmemory/MainSuccessful.java

RoadMap
======
 - Improve state machine 'onUnhandled', 'onTransition' available callback methods
