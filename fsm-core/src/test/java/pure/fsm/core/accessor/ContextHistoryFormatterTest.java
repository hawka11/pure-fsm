package pure.fsm.core.accessor;

import org.junit.Test;
import pure.fsm.core.Context;
import pure.fsm.core.context.BaseContext;
import pure.fsm.core.event.Event;
import pure.fsm.core.event.EventVisitor;
import pure.fsm.core.state.BaseNonFinalState;
import pure.fsm.core.state.State;
import pure.fsm.core.state.StateFactory;
import pure.fsm.core.state.SuccessFinalState;

import java.time.LocalDateTime;

import static pure.fsm.core.accessor.ContextHistoryFormatter.HISTORY_FORMATTER;
import static pure.fsm.core.context.BaseContext.builder;

public class ContextHistoryFormatterTest {

    @Test
    public void testLog() throws InterruptedException {
        TestContext initial = new TestContext(builder().transitioned(LocalDateTime.now()).currentState(new TestInitialState()).stateMachineId("1"));
        Context middle = initial.transition(new SuccessFinalState(), new TestEvent());
        Context end = middle.transition(new SuccessFinalState(), new TestEvent());

        String output = HISTORY_FORMATTER.toTransitionString(end);

        System.out.print(output);
    }

    private static class TestEvent implements Event<TestContext, TestEventVisitor> {

        @Override
        public State accept(TestContext context, TestEventVisitor visitor) {
            return null;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    private static class TestInitialState extends BaseNonFinalState {

        @Override
        public StateFactory factory() {
            return null;
        }

        @Override
        public State handle(Context context, Event event) {
            return null;
        }
    }

    private static class TestContext extends BaseContext {

        public TestContext(BaseContextBuilder builder) {
            super(builder);
        }

        @Override
        public Context transition(State newState, Event event) {
            return new TestContext(transitionWith(newState, event));
        }
    }

    private static interface TestEventVisitor extends EventVisitor {
    }
}