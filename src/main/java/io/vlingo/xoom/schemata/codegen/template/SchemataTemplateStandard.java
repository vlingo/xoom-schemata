package io.vlingo.xoom.schemata.codegen.template;

import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum SchemataTemplateStandard implements TemplateStandard {
  SCHEMA_TYPE((parameters) -> Template.SCHEMA_TYPE.filename, (name, parameters) -> Template.SCHEMA_TYPE.filename);

  private final Function<TemplateParameters, String> templateFileRetriever;
  private final BiFunction<String, TemplateParameters, String> nameResolver;

  SchemataTemplateStandard(final Function<TemplateParameters, String> templateFileRetriever, final BiFunction<String, TemplateParameters, String> nameResolver) {
    this.templateFileRetriever = templateFileRetriever;
    this.nameResolver = nameResolver;
  }

  public String retrieveTemplateFilename(final TemplateParameters parameters) {
    return this.templateFileRetriever.apply(parameters);
  }

  public String resolveClassname() {
    return this.resolveClassname("");
  }

  public String resolveClassname(final String name) {
    return this.resolveClassname(name, null);
  }

  public String resolveClassname(final TemplateParameters parameters) {
    return this.resolveClassname(null, parameters);
  }

  public String resolveClassname(final String name, final TemplateParameters parameters) {
    return this.nameResolver.apply(name, parameters);
  }

  public String resolveFilename(final TemplateParameters parameters) {
    return this.resolveFilename(null, parameters);
  }

  public String resolveFilename(final String name, final TemplateParameters parameters) {
    return this.nameResolver.apply(name, parameters);
  }
}
