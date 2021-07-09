package io.vlingo.xoom.schemata.codegen.ast.types;

import io.vlingo.xoom.schemata.codegen.ast.Node;

public interface Type extends Node {

  default boolean isComplexType() {
    return this instanceof ComplexType;
  }

  default boolean containsComplexType() {
    return this instanceof ArrayType && ((ArrayType) this).elementType instanceof ComplexType;
  }

}
