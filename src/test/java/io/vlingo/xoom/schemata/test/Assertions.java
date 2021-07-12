package io.vlingo.xoom.schemata.test;

import io.vlingo.xoom.common.Completes;

import java.util.function.Consumer;

import static org.junit.Assert.assertNotEquals;

public class Assertions {

  /**
   * Makes assertions on an outcome of an asynchronous operation.
   * @param completes the eventual completion of an asynchronous operation
   * @param assertions the consumer of the completed operation that will be called to make assertions
   * @param <T> the type of the outcome
   */
  public static <T> void assertCompletes(Completes<T> completes, Consumer<T> assertions) {
    final T outcome = completes.await(2000);
    assertNotEquals(null, outcome);
    assertions.accept(outcome);
  }

}
