package io.vlingo.xoom.schemata.test;

import io.vlingo.xoom.common.Completes;

import java.util.function.BiConsumer;
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

  /**
   * Makes assertions on outcomes of two asynchronous operations.
   * @param completes1 the eventual completion of the first asynchronous operation
   * @param completes2 the eventual completion of the second asynchronous operation
   * @param assertions the consumer of completed operations that will be called to make assertions
   * @param <T> the type of the first outcome
   * @param <R> the type of the second outcome
   */
  public static <T, R> void assertCompletes(Completes<T> completes1, Completes<R> completes2, BiConsumer<T, R> assertions) {
    final T outcome1 = completes1.await(2000);
    final R outcome2 = completes2.await(2000);
    assertNotEquals(null, outcome1);
    assertNotEquals(null, outcome2);
    assertions.accept(outcome1, outcome2);
  }

  /**
   * Makes sure the value isn't null before passing it to the assertions callback.
   * @param value the value to be used with assertions
   * @param assertions the consumer of a non-null value that will be called to make assertions
   * @param <T> the type of the value
   */
  public static <T> void assertOnNotNull(T value, Consumer<T> assertions) {
    assertNotEquals(null, value);
    assertions.accept(value);
  }

}
