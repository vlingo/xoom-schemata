package io.vlingo.xoom.schemata.codegen.template.schematype;

import io.vlingo.xoom.codegen.parameter.ParameterLabel;

public enum SchemaTypeParameterLabel implements ParameterLabel {
  TYPE_DEFINITION("typeDefinition"),
  LANGUAGE("language"),
  VERSION("version");

  private final String key;

  private SchemaTypeParameterLabel(String key) {
    this.key = key;
  }
}
