package io.vlingo.xoom.schemata.codegen.template.schematype.java;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.lattice.model.Command;
import io.vlingo.xoom.lattice.model.DomainEvent;
import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypePackage;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypeTemplateData;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaSchemaTypeTemplateData extends SchemaTypeTemplateData {

  private final TypeDefinition type;
  private final String version;

  public static TemplateData from(final TypeDefinition type, final String version) {
    return new JavaSchemaTypeTemplateData(type, version);
  }

  private JavaSchemaTypeTemplateData(final TypeDefinition type, final String version) {
    this.type = type;
    this.version = version;
  }

  @Override
  public TemplateParameters parameters() {
    List<JavaField> fields = fields();
    return TemplateParameters
            .with(JavaSchemaTypeTemplateParameter.PACKAGE, packageName())
            .and(JavaSchemaTypeTemplateParameter.TYPE_NAME, typeName())
            .and(JavaSchemaTypeTemplateParameter.BASE_TYPE_NAME, baseTypeName())
            .and(JavaSchemaTypeTemplateParameter.NEEDS_CONSTRUCTOR, needsConstructor(fields))
            .and(JavaSchemaTypeTemplateParameter.NEEDS_DEFAULT_CONSTRUCTOR, needsDefaultConstructor(fields))
            .and(JavaSchemaTypeTemplateParameter.FIELDS, fields)
            .and(JavaSchemaTypeTemplateParameter.COMPUTED_FIELDS, computedFields(fields))
            .and(JavaSchemaTypeTemplateParameter.IMPORTS, imports(fields));
  }

  private List<JavaField> fields() {
    return type.children.stream()
            .filter(c -> c instanceof FieldDefinition)
            .map(c -> JavaField.from((FieldDefinition) c, type, version))
            .collect(Collectors.toList());
  }

  private List<JavaField> computedFields(final List<JavaField> fields) {
    return fields.stream().filter(f -> f.isComputed).collect(Collectors.toList());
  }

  private String packageName() {
    return SchemaTypePackage.from(type.fullyQualifiedTypeName, type.category.name().toLowerCase(), ".").name();
  }

  private String typeName() {
    return type.typeName;
  }

  private String baseTypeName() {
    return baseClass().map(c -> c.getSimpleName()).orElse(null);
  }

  private String basePackageName() {
    return baseClass().map(c -> c.getName()).orElse(null);
  }

  private Optional<Class<?>> baseClass() {
    switch (type.category) {
      case Event:
        return Optional.of(DomainEvent.class);
      case Command:
        return Optional.of(Command.class);
      default:
        return Optional.empty();
    }
  }

  private boolean needsConstructor(final List<JavaField> fields) {
    return fields.stream().anyMatch(f -> !f.isComputed);
  }

  private boolean needsDefaultConstructor(final List<JavaField> fields) {
    return fields.size() != 0 && fields.stream().allMatch(f -> f.isComputed || f.defaultValue != null);
  }

  private List<String> imports(final List<JavaField> fields) {
    return Stream.concat(Stream.of(basePackageName()), fieldImports(fields).stream())
            .filter(i -> i != null)
            .sorted()
            .collect(Collectors.toList());
  }

  private Set<String> fieldImports(final List<JavaField> fields) {
    return fields.stream()
            .flatMap(f -> f.typeImports.stream())
            .collect(Collectors.toSet());
  }
}
