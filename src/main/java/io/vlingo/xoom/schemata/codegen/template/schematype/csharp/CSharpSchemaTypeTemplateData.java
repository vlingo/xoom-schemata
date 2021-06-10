package io.vlingo.xoom.schemata.codegen.template.schematype.csharp;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypePackage;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypeTemplateData;
import io.vlingo.xoom.schemata.model.Category;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    List<CSharpProperty> properties = properties();
    return TemplateParameters
            .with(CSharpSchemaTypeTemplateParameter.NAMESPACE, namespace())
            .and(CSharpSchemaTypeTemplateParameter.IMPORTS, imports(properties))
            .and(CSharpSchemaTypeTemplateParameter.TYPE_NAME, typeName())
            .and(CSharpSchemaTypeTemplateParameter.BASE_TYPE_NAME, baseTypeName())
            .and(CSharpSchemaTypeTemplateParameter.PROPERTIES, properties)
            .and(CSharpSchemaTypeTemplateParameter.NEEDS_CONSTRUCTOR, needsConstructor(properties))
            .and(CSharpSchemaTypeTemplateParameter.NEEDS_DEFAULT_CONSTRUCTOR, needsDefaultConstructor(properties));
  }

  private String namespace() {
    return SchemaTypePackage.from(type.fullyQualifiedTypeName, categoryNamespaceSegment(type.category), ".")
            .withTitleCaseSegmentFormatter()
            .name();
  }

  private String categoryNamespaceSegment(final Category category) {
    switch (category) {
      case Data:
        return "Data";
      default:
        return category.name() + "s";
    }
  }

  private List<String> imports(final List<CSharpProperty> properties) {
    final Stream<String> propertyImports = properties.stream().flatMap(p -> p.namespaceImports.stream());
    final boolean usesSystem = properties.stream().anyMatch(p -> p.constructorInitializer.startsWith("DateTimeOffset."));
    final boolean usesVersion = properties.stream().anyMatch(p -> p.constructorInitializer.startsWith("SemanticVersion."));
    final boolean usesLatticeModel = type.category.equals(Category.Event) || type.category.equals(Category.Command);
    return Stream.concat(Stream.of("System", "Vlingo.Lattice.Model", "Vlingo.Xoom.Common.Version"), propertyImports)
            .filter(i -> !(i.equals("System") && !usesSystem))
            .filter(i -> !(i.equals("Vlingo.Xoom.Common.Version") && !usesVersion))
            .filter(i -> !(i.equals("Vlingo.Lattice.Model") && !usesLatticeModel))
            .collect(Collectors.toSet())
            .stream().sorted().collect(Collectors.toList());
  }

  private String typeName() {
    return type.typeName;
  }

  private String baseTypeName() {
    switch (type.category) {
      case Event:
        return "DomainEvent";
      case Command:
        return "Command";
      default:
        return null;
    }
  }

  private List<CSharpProperty> properties() {
    return type.children.stream()
            .filter(c -> c instanceof FieldDefinition)
            .map(c -> CSharpProperty.from((FieldDefinition) c, type, version))
            .collect(Collectors.toList());
  }

  private boolean needsConstructor(final List<CSharpProperty> properties) {
    return properties.stream().anyMatch(f -> !f.isComputed);
  }

  private boolean needsDefaultConstructor(final List<CSharpProperty> properties) {
    return properties.size() != 0 && properties.stream().allMatch(f -> f.isComputed || f.defaultValue != null);
  }
}
