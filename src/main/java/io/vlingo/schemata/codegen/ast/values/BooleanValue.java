package io.vlingo.schemata.codegen.ast.values;

public class BooleanValue implements Value<Boolean> {
  final Boolean value;

  private BooleanValue(Boolean value) {
    this.value = value;
  }

  public static BooleanValue of(Boolean value) {
    return new BooleanValue(value);
  }

  @Override
  public String name() {
    return value.toString();
  }

  @Override
  public Boolean value() {
    return value;
  }
}
