package io.vlingo.xoom.schemata.codegen.template.schematype.csharp;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypeTemplateData;

public class CSharpSchemaTypeTemplateData extends SchemaTypeTemplateData {

  private final TypeDefinition type;
  private final String version;

  public static TemplateData from(final TypeDefinition type, final String version) {
    return new CSharpSchemaTypeTemplateData(type, version);
  }

  private CSharpSchemaTypeTemplateData(final TypeDefinition type, final String version) {
    this.type = type;
    this.version = version;
  }

  @Override
  public TemplateParameters parameters() {
    return TemplateParameters.empty();
  }
}
