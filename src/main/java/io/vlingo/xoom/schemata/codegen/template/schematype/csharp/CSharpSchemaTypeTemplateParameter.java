package io.vlingo.xoom.schemata.codegen.template.schematype.csharp;

import io.vlingo.xoom.codegen.template.ParameterKey;

public enum CSharpSchemaTypeTemplateParameter implements ParameterKey {
  NAMESPACE("namespace"),
  TYPE_NAME("typeName"),
  BASE_TYPE_NAME("baseTypeName"),
  IMPORTS("imports"),
  PROPERTIES("properties"),
  NEEDS_CONSTRUCTOR("needsConstructor"),
  NEEDS_DEFAULT_CONSTRUCTOR("needsDefaultConstructor");

  public final String key;

  private CSharpSchemaTypeTemplateParameter(final String key) {
    this.key = key;
  }

  public String value() {
    return this.key;
  }
  }
