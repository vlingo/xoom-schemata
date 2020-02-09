package io.vlingo.schemata.codegen.ast.values;

import io.vlingo.schemata.codegen.ast.Node;

public interface Value<T> extends Node {

  public T value();
}
