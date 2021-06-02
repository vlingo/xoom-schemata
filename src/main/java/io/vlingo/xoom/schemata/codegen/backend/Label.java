package io.vlingo.xoom.schemata.codegen.backend;

import io.vlingo.xoom.codegen.parameter.ParameterLabel;

public enum Label implements ParameterLabel {
  TYPE_DEFINITION("typeDefinition"),
  VERSION("version");

  private final String key;

  private Label(String key) {
    this.key = key;
  }
}
