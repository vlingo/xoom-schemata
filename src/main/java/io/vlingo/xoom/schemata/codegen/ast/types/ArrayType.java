package io.vlingo.xoom.schemata.codegen.ast.types;

public class ArrayType implements Type {
  public final Type elementType;

  public ArrayType(final Type elementType) {
    this.elementType = elementType;
  }

  @Override
  public String toString() {
    return "ArrayType [name=" + name() + "]";
  }

  @Override
  public String name() {
    return elementType.name() + "[]";
  }
}
