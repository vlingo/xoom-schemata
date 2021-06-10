package io.vlingo.xoom.schemata.codegen.template.schematype.csharp;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.types.*;
import io.vlingo.xoom.schemata.codegen.ast.values.ListValue;
import io.vlingo.xoom.schemata.codegen.ast.values.NullValue;
import io.vlingo.xoom.schemata.codegen.ast.values.SingleValue;
import io.vlingo.xoom.schemata.codegen.ast.values.Value;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypeTemplateData;
import io.vlingo.xoom.schemata.model.Category;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

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
    List<Property> properties = properties();
    return TemplateParameters
            .with(CSharpSchemaTypeTemplateParameter.NAMESPACE, namespace())
            .and(CSharpSchemaTypeTemplateParameter.IMPORTS, imports())
            .and(CSharpSchemaTypeTemplateParameter.TYPE_NAME, typeName())
            .and(CSharpSchemaTypeTemplateParameter.BASE_TYPE_NAME, baseTypeName())
            .and(CSharpSchemaTypeTemplateParameter.PROPERTIES, properties)
            .and(CSharpSchemaTypeTemplateParameter.NEEDS_CONSTRUCTOR, needsConstructor(properties))
            .and(CSharpSchemaTypeTemplateParameter.NEEDS_DEFAULT_CONSTRUCTOR, needsDefaultConstructor(properties));
  }

  private String namespace() {
    return namespace(categoryNamespaceSegment(type.category));
  }

  private String namespace(String lastSegment) {
    return packageSegments(type.fullyQualifiedTypeName, lastSegment)
            .stream()
            .map(p -> p.substring(0, 1).toUpperCase() + p.substring(1))
            .collect(joining("."));
  }

  private String categoryNamespaceSegment(Category category) {
    switch (category) {
      case Data:
        return "Data";
      default:
        return category.name() + "s";
    }
  }

  private List<String> imports() {
    List<Property> properties = properties();
    Stream<String> propertyImports = properties.stream().map(p -> p.namespaceImport).filter(i -> i != null);
    return Stream.concat(Stream.of("System", "Vlingo.Lattice.Model", "Vlingo.Xoom.Common.Version"), propertyImports)
            .filter(i -> !i.equals("System") || properties.stream().anyMatch(p -> p.constructorInitializer.startsWith("DateTimeOffset.")))
            .filter(i -> !i.equals("Vlingo.Xoom.Common.Version") || properties.stream().anyMatch(p -> p.constructorInitializer.startsWith("SemanticVersion.")))
            .filter(i -> !i.equals("Vlingo.Lattice.Model") || type.category.equals(Category.Event))
            .collect(Collectors.toSet())
            .stream().sorted().collect(Collectors.toList());
  }

  private String typeName() {
    return type.typeName;
  }

  private String baseTypeName() {
    switch(type.category) {
      case Event:
        return "DomainEvent";
      default:
        return null;
    }

  }

  private List<Property> properties() {
    return type.children.stream()
            .filter(c -> c instanceof FieldDefinition)
            .map(c -> (FieldDefinition) c)
            .map(this::toProperty)
            .collect(Collectors.toList());
  }

  private boolean needsConstructor(final List<Property> properties) {
    return properties.stream().anyMatch(f -> !f.isComputed);
  }

  private boolean needsDefaultConstructor(final List<Property> properties) {
    return properties.size() != 0 && properties.stream().allMatch(f -> f.isComputed || f.defaultValue != null);
  }

  private String initializationOf(final FieldDefinition field, final TypeDefinition owner) {
    Type type = field.type;
    if (type instanceof ComputableType) {
      switch (((ComputableType) type).typeName) {
        case "type":
          return String.format("\"%s\"", owner.typeName);
        case "version":
          return String.format("SemanticVersion.toValue(\"%s\")", this.version);
        case "timestamp":
          return "DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()";
      }
    }
    return field.name;
  }

  @Override
  protected String array(final ArrayType type) {
    return type(type.elementType) + "[]";
  }

  @Override
  protected String primitive(final BasicType basicType) {
    switch (basicType.typeName) {
      case "boolean":
        return "bool";
      case "byte":
      case "char":
      case "short":
      case "int":
      case "long":
      case "float":
      case "double":
      case "string":
        return basicType.typeName;
      default:
        return "object";
    }
  }

  @Override
  protected String computable(final ComputableType computableType) {
    switch (computableType.typeName) {
      case "type":
        return "string";
      case "timestamp":
        return "long";
      case "version":
        return "int";
      default:
        return "object";
    }
  }

  @Override
  protected String complex(final ComplexType complexType) {
    return complexType.name();
  }

  private String cSharpLiteralOf(FieldDefinition field) {
    Value<?> value = field.defaultValue.orElseGet(NullValue::new);

    if(value instanceof NullValue) {
      return null;
    }

    if(value instanceof SingleValue) {
      return cSharpLiteralOf((SingleValue<?>) value);
    }

    if(value instanceof ListValue) {
      return cSharpLiteralOf((ListValue<?>) value);
    }

    throw new IllegalStateException("Unsupported value type encountered");
  }

  private String cSharpLiteralOf(final SingleValue<?> value) {
    return value.value().toString();
  }

  @SuppressWarnings("unchecked")
  private String cSharpLiteralOf(final ListValue value) {
    return value.value().stream()
            .map(e -> ((SingleValue<?>) e).value())
            .collect(joining(", ", "{ ", " }"))
            .toString();
  }

  private String namespaceImport(Type type) {
    if (type instanceof ArrayType) {
      return namespaceImport(((ArrayType) type).elementType);
    }
    if (type instanceof ComplexType) {
      return namespace(((ComplexType) type).category.name());
    }
    return null;
  }

  private Property toProperty(final FieldDefinition field) {
    return new Property(
            type(field.type),
            namespaceImport(field.type),
            field.name.substring(0, 1).toUpperCase() + field.name.substring(1),
            field.name,
            cSharpLiteralOf(field),
            initializationOf(field, type),
            field.type instanceof ComputableType
    );
  }

  public static class Property {
    public final String type;
    public final String namespaceImport;
    public final String name;
    public final String argumentName;
    public final String defaultValue;
    public final String constructorInitializer;
    public final boolean isComputed;

    public Property(final String type, final String namespaceImport, final String name, final String argumentName, final String defaultValue, final String constructorInitializer, final boolean isComputed) {
      this.type = type;
      this.namespaceImport = namespaceImport;
      this.name = name;
      this.argumentName = argumentName;
      this.defaultValue = defaultValue;
      this.constructorInitializer = constructorInitializer;
      this.isComputed = isComputed;
    }
  }
}
