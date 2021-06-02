package io.vlingo.xoom.schemata.codegen.backend;

import io.vlingo.xoom.codegen.template.ParameterKey;

public enum TemplateParameter implements ParameterKey {
  PACKAGE("package"),
  TYPE_NAME("typeName"),
  BASE_TYPE("baseType"),
  FIELDS("fields"),
  COMPUTED_FIELDS("computedFields"),
  NEEDS_CONSTRUCTOR("needsConstructor"),
  NEEDS_DEFAULT_CONSTRUCTOR("needsDefaultConstructor"),
  IMPORTS("imports");

  public final String key;

  private TemplateParameter(final String key) {
    this.key = key;
  }

  public String value() {
    return this.key;
  }
  }
