package io.vlingo.schemata.codegen.ast.values;

public class StringValue implements Value {
  final String value;

  private StringValue(String value) {
    this.value = value;
  }

  public static StringValue of(String value) {
    return new StringValue(value);
  }

  @Override
  public String name() {
    return value;
  }
}