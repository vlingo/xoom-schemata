package io.vlingo.xoom.schemata.test.symbio;

import io.vlingo.xoom.actors.Logger;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.actors.testkit.TestUntil;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * StateStore Dispatcher implementation that allows to synchronize based on expected dispatchable types.
 *
 * <code>
 *   FakeStateStoreDispatcher dispatcher = new FakeStateStoreDispatcher(logger);
 *   MyEntity entity = dispatcher.onceProjected(MyView.class, () -> {
 *     // some code that's expected to trigger a projection to MyView
 *     return MyEntity.empty();
 *   });
 * </code>
 */
public class FakeStateStoreDispatcher implements Dispatcher<Dispatchable<? extends Entry<?>, ? extends State<?>>> {
  private final Logger logger;
  private DispatcherControl control = null;
  final private Map<String, List<TestUntil>> expectations = new HashMap<>();
  final private AccessSafely access = AccessSafely.immediately()
          .writingWith("registerExpectation", (String type, TestUntil expectation) -> expectations.compute(type, (key, oldExpectationStack) -> {
            List<TestUntil> expectationStack = oldExpectationStack == null ? new ArrayList<>() : oldExpectationStack;
            expectationStack.add(expectation);
            return expectationStack;
          }))
          .writingWith("matchExpectation", (String type) -> expectations.forEach((expectedType, expectationStack) -> {
            if (expectedType.equals(type)) {
              final TestUntil expectation = expectationStack.get(expectationStack.size() - 1);
              expectation.happened();
              if (expectation.remaining() == 0) {
                expectationStack.remove(expectation);
              }
            }
          }));

  public FakeStateStoreDispatcher(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void controlWith(DispatcherControl dispatcherControl) {
    control = dispatcherControl;
  }

  @Override
  public void dispatch(Dispatchable<? extends Entry<?>, ? extends State<?>> dispatchable) {
    dispatchable.state().ifPresent(state -> logger.info(String.format("DISPATCHED %s", state.id)));
    if (control != null) {
      control.confirmDispatched(dispatchable.id(), (Result result, String dispatchId) -> {
        logger.info(String.format("DISPATCH %s for %s", result, dispatchId));
        dispatchable.state().ifPresent(state -> access.writeUsing("matchExpectation", state.type));
      });
    }
  }

  /**
   * Calls the supplier and returns the result as soon as given type is projected.
   */
  public <R> R onceProjected(Class<?> type, Supplier<R> supplier) {
    return onceProjected(type, 1, supplier);
  }

  /**
   * Calls the supplier and returns the result as soon as given type is projected a given number of times.
   */
  public <R> R onceProjected(Class<?> type, int times, Supplier<R> supplier) {
    final TestUntil expectation = TestUntil.happenings(times);
    access.writeUsing("registerExpectation", type.getCanonicalName(), expectation);
    R result = supplier.get();
    // Do not wait indefinitely, but long enough for most delays
    expectation.completesWithin(30000);
    return result;
  }
}
