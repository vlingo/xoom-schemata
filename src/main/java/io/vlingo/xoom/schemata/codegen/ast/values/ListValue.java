package io.vlingo.xoom.schemata.codegen.ast.values;

import java.util.List;

@SuppressWarnings("rawtypes")
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
