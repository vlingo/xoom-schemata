package io.vlingo.schemata.codegen.ast.values;

public class NullValue implements Value {
  private static final NullValue INSTANCE = new NullValue();

  @Override
  public String name() {
    return "";
  }

  public static NullValue get() {
    return INSTANCE;
  }
}
