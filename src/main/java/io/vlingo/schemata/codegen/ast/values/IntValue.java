package io.vlingo.schemata.codegen.ast.values;

public class IntValue implements Value {
  final Integer value;

  private IntValue(Integer value) {
    this.value = value;
  }

  public static IntValue of(Integer value) {
    return new IntValue(value);
  }

  @Override
  public String name() {
    return value.toString();
  }
}
