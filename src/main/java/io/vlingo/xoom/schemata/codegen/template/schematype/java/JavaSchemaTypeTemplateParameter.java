package io.vlingo.xoom.schemata.codegen.template.schematype.java;

import io.vlingo.xoom.codegen.template.ParameterKey;

public enum JavaSchemaTypeTemplateParameter implements ParameterKey {
  PACKAGE("package"),
  TYPE_NAME("typeName"),
  BASE_TYPE_NAME("baseTypeName"),
  FIELDS("fields"),
  COMPUTED_FIELDS("computedFields"),
  NEEDS_CONSTRUCTOR("needsConstructor"),
  NEEDS_DEFAULT_CONSTRUCTOR("needsDefaultConstructor"),
  IMPORTS("imports");

  public final String key;

  private JavaSchemaTypeTemplateParameter(final String key) {
    this.key = key;
  }

  public String value() {
    return this.key;
  }
  }
