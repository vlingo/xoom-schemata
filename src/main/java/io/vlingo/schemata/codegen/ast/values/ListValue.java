package io.vlingo.schemata.codegen.ast.values;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class ListValue<T extends List<? extends SingleValue>> implements Value<T> {
  public final T value;

  public ListValue(T value) {
    this.value = value;
  }

  @Override
  public String name() {
    return value.getClass().getSimpleName();
  }

  @Override
  public T value() {
    return value;
  }
}
