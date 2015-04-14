package pure.fsm.core.accessor;

public class ContextHistoryFormatterTest {

   /* @Test
    public void testLog() throws InterruptedException {
        Context initial =  new TestContext(builder().transitioned(LocalDateTime.now()).currentState(new TestInitialState()).stateMachineId("1"));
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


    private static interface TestEventVisitor extends EventVisitor {
    }*/
}