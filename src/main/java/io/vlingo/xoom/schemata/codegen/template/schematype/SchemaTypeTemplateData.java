package io.vlingo.xoom.schemata.codegen.template.schematype;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.schemata.Schemata;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.template.schematype.csharp.CSharpSchemaTypeTemplateData;
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
      case "csharp":
        return CSharpSchemaTypeTemplateData.from(type, version);
      default:
        throw new IllegalArgumentException("Unsupported language: " + language);
    }
  }

  @Override
  public TemplateStandard standard() {
    return SchemataTemplateStandard.SCHEMA_TYPE;
  }

  protected List<String> packageSegments(String reference, String category) {
    final String[] referenceParts = reference.split(Schemata.ReferenceSeparator);
    if (referenceParts.length < Schemata.MinReferenceParts) {
      throw new IllegalArgumentException("Invalid fully qualified type name. Valid type names look like this <organization>:<unit>:<context namespace>:<type name>[:<version>].");
    }
    final String namespace = referenceParts[2];
    final String className = referenceParts[3];

    final String basePackage = namespace + "." + category;
    final String localPackage = className.contains(".") ? className.substring(0, className.lastIndexOf('.')) : "";
    return Arrays.asList((localPackage.length() > 0
            ? basePackage + "." + localPackage
            : basePackage).split("\\."));
  }
}
