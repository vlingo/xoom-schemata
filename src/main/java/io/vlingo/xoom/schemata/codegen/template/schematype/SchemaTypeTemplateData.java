package io.vlingo.xoom.schemata.codegen.template.schematype;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.template.schematype.java.JavaSchemaTypeTemplateData;
import io.vlingo.xoom.schemata.codegen.template.SchemataTemplateStandard;

import java.util.Arrays;
import java.util.List;

public abstract class SchemaTypeTemplateData extends TemplateData {

  public static List<TemplateData> from(final String language, final TypeDefinition type, final String version) {
    return Arrays.asList(create(language, type, version));
  }

  private static TemplateData create(final String language, final TypeDefinition type, final String version) {
    switch (language) {
      case "java":
        return JavaSchemaTypeTemplateData.from(type, version);
      default:
        throw new IllegalArgumentException("Unsupported language: " + language);
    }
  }

  @Override
  public TemplateStandard standard() {
    return SchemataTemplateStandard.SCHEMA_TYPE;
  }
}
