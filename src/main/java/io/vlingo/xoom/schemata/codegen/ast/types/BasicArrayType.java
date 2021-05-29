package io.vlingo.xoom.schemata.codegen.ast.types;

public class BasicArrayType extends BasicType {
  public BasicArrayType(String typeName) {
    super(typeName);
  }

  @Override
  public boolean isArrayType() {
    return true;
  }

  @Override
  public String toString() {
    return "BasicArrayType [typeName=" + typeName + "]";
  }
}
