package io.vlingo.xoom.schemata.codegen.ast.values;

import io.vlingo.xoom.schemata.codegen.ast.Node;

public interface Value<T> extends Node {

  public T value();
}
