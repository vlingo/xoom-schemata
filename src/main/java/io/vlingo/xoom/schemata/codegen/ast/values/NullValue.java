package io.vlingo.xoom.schemata.codegen.ast.values;

@SuppressWarnings("rawtypes")
public class NullValue implements Value {
  private static final NullValue INSTANCE = new NullValue();

  @Override
  public String name() {
    return "";
  }

  public static NullValue get() {
    return INSTANCE;
  }

  @Override
  public Object value() {
    return null;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof NullValue)
      return true;
    return false;
  }

  @Override
  public String toString() {
    return "NullValue []";
  }
}
