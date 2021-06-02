package io.vlingo.xoom.schemata.codegen.backend;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.content.TextBasedContent;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.TemplateProcessingStep;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.common.Success;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.processor.Processor;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;

import java.util.stream.Collectors;

public class CodeGenBackend implements Backend {

  private final TemplateProcessingStep templateProcessingStep;
  private final String language;

  public CodeGenBackend(final TemplateProcessingStep templateProcessingStep, final String language) {
    this.templateProcessingStep = templateProcessingStep;
    this.language = language;
  }

  @Override
  public Outcome<SchemataBusinessException, String> generateOutput(final Node node, final String version) {
    TypeDefinition typeDefinition = Processor.requireBeing(node, TypeDefinition.class);
    return Success.of(generateType(typeDefinition, version));
  }

  private String generateType(final TypeDefinition typeDefinition, final String version) {
    CodeGenerationContext context = CodeGenerationContext.with(
            CodeGenerationParameters
                    .from(CodeGenerationParameter.ofObject(Label.TYPE_DEFINITION, typeDefinition))
                    .add(CodeGenerationParameter.of(Label.VERSION, version))
                    .add(CodeGenerationParameter.of(Label.LANGUAGE, language))
    );
    templateProcessingStep.process(context);
    return context.contents().stream()
            .filter(c -> c instanceof TextBasedContent)
            .map(c -> ((TextBasedContent) c).text)
            .collect(Collectors.joining("\n"));
  }
}
