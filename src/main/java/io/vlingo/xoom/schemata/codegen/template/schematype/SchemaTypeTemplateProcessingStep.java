package io.vlingo.xoom.schemata.codegen.template.schematype;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.dialect.Dialect;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;

import java.util.List;

public class SchemaTypeTemplateProcessingStep extends TemplateProcessingStep {
  @Override
  protected List<TemplateData> buildTemplatesData(final CodeGenerationContext context) {
    return SchemaTypeTemplateData.from(
            context.parameterOf(SchemaTypeParameterLabel.LANGUAGE),
            context.parameterObjectOf(SchemaTypeParameterLabel.TYPE_DEFINITION),
            context.parameterOf(SchemaTypeParameterLabel.VERSION)
    );
  }

  @Override
  protected Dialect resolveDialect(final CodeGenerationContext context) {
    String language = context.parameterOf(SchemaTypeParameterLabel.LANGUAGE);
    switch (language) {
      case "java":
        return Dialect.JAVA;
      case "csharp":
        return Dialect.C_SHARP;
      default:
        throw new IllegalArgumentException("Unsupported language: " + language);
    }
  }
}
