package io.vlingo.schemata.codegen.ast.values;

public class ValueImpl<T> implements Value<T> {
  public final T value;

  public ValueImpl(T value) {
    this.value = value;
  }

  @Override
  public String name() {
    return value.toString();
  }

  @Override
  public T value() {
    return value;
  }
}
