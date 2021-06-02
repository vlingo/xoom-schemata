package io.vlingo.xoom.schemata.codegen.backend;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.lattice.model.DomainEvent;
import io.vlingo.xoom.schemata.Schemata;
import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.types.BasicType;
import io.vlingo.xoom.schemata.codegen.ast.types.ComputableType;
import io.vlingo.xoom.schemata.codegen.ast.types.Type;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.ast.values.ListValue;
import io.vlingo.xoom.schemata.codegen.ast.values.NullValue;
import io.vlingo.xoom.schemata.codegen.ast.values.SingleValue;
import io.vlingo.xoom.schemata.codegen.ast.values.Value;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.String;

import static java.util.stream.Collectors.joining;

public class SchemaTypeTemplateData extends TemplateData {

  private final TypeDefinition type;
  private final String version;

  public static List<TemplateData> from(final TypeDefinition type, final String version) {
    return Arrays.asList(
            new SchemaTypeTemplateData(type, version)
    );
  }

  private SchemaTypeTemplateData(final TypeDefinition type, final String version) {
    this.type = type;
    this.version = version;
  }

  @Override
  public TemplateParameters parameters() {
    List<Field> fields = type.children.stream()
            .filter(c -> c instanceof FieldDefinition)
            .map(c -> (FieldDefinition) c)
            .map(this::toField)
            .collect(Collectors.toList());
    Class<?> baseType = baseClassOf(type);
    return TemplateParameters
            .with(TemplateParameter.PACKAGE, packageOf(type.category.name().toLowerCase(), type.fullyQualifiedTypeName))
            .and(TemplateParameter.TYPE_NAME, type.typeName)
            .and(TemplateParameter.BASE_TYPE, baseType.getSimpleName())
            .and(TemplateParameter.NEEDS_CONSTRUCTOR, fields.stream().anyMatch(f -> !f.isComputed))
            .and(TemplateParameter.NEEDS_DEFAULT_CONSTRUCTOR, fields.size() != 0 && fields.stream().allMatch(f -> f.isComputed || f.initializer != ""))
            .and(TemplateParameter.FIELDS, fields.stream().collect(Collectors.toList()))
            .and(TemplateParameter.COMPUTED_FIELDS, fields.stream().filter(f -> f.isComputed).collect(Collectors.toList()))
            .and(TemplateParameter.IMPORTS, imports(baseType, fields));
  }

  private List<String> imports(final Class<?> baseType, final List<Field> fields) {
    return Stream.concat(Arrays.asList(baseType.getName()).stream(), fields.stream().map(f -> {
      if (f.type.equals("String") || f.type.equals("String[]")) {
        return "java.lang.String";
      }
      return null;
    }).filter(f -> f != null)).collect(Collectors.toSet()).stream().sorted().collect(Collectors.toList());
  }

  private String initializationOf(final FieldDefinition definition, final TypeDefinition owner, final String version) {
    Type type = definition.type;
    if (type instanceof ComputableType) {
      switch (((ComputableType) type).typeName) {
        case "type":
          return String.format("\"%s\"", owner.typeName);
        case "version":
          return String.format("io.vlingo.xoom.common.version.SemanticVersion.toValue(\"%s\")", version);
        case "timestamp":
          return "System.currentTimeMillis()";
      }
    }

    return String.format("%s", definition.name);
  }

  public class Field {

    public final String modifiers;
    public final String type;
    public final String name;
    public final String initializer;
    public final String constructorInitializer;
    public final boolean isComputed;

    public Field(final String modifiers, final String type, final String name, final String initializer, final String constructorInitializer, final boolean isComputed) {
      this.modifiers = modifiers;
      this.type = type;
      this.name = name;
      this.initializer = initializer != null ? initializer : "";
      this.constructorInitializer = constructorInitializer;
      this.isComputed = isComputed;
    }
  }

  private Field toField(final FieldDefinition field) {
    return new Field(
            field.hasDefaultValue() ? "public" : "public final",
            type(field.type),
            field.name,
            javaLiteralOf(field),
            initializationOf(field, type, version),
            field.type instanceof ComputableType
    );
  }

  private String type(final Type type) {
    if (type instanceof BasicType) {
      return primitive((BasicType) type);
    } else if (type instanceof ComputableType) {
      return computable((ComputableType) type);
    }
    return type.name();
  }

  @Override
  public TemplateStandard standard() {
    return SchemataTemplateStandard.SCHEMA_TYPE;
  }

  private String primitive(final BasicType basicType) {
    String result = null;
    switch (basicType.typeName) {
      case "boolean":
      case "byte":
      case "char":
      case "short":
      case "int":
      case "long":
      case "float":
      case "double":
        result = basicType.typeName;
        break;
      case "string":
        result = "String";
        break;
      default:
        result = "Object";
        break;
    }

    return basicType.isArrayType() ? result + "[]" : result;
  }

  private String computable(final ComputableType computableType) {
    switch (computableType.typeName) {
      case "type":
        return "String";
      case "timestamp":
        return "long";
      case "version":
        return "int";
      default:
        return "Object";
    }
  }

  private String packageOf(final String category, final String reference) {
    String[] referenceParts = reference.split(Schemata.ReferenceSeparator);
    if (referenceParts.length < Schemata.MinReferenceParts) {
      throw new IllegalArgumentException("Invalid fully qualified type name. Valid type names look like this <organization>:<unit>:<context namespace>:<type name>[:<version>].");
    }
    final String namespace = referenceParts[2];
    final String className = referenceParts[3];

    final String basePackage = namespace + "." + category;
    final String localPackage = className.contains(".") ? className.substring(0, className.lastIndexOf('.')) : "";
    return localPackage.length() > 0
            ? basePackage + "." + localPackage
            : basePackage;
  }

  private String javaLiteralOf(final FieldDefinition definition) {
    Value value = definition.defaultValue.orElseGet(NullValue::new);

    if(value instanceof NullValue) {
      return null;
    }

    if(value instanceof SingleValue) {
      return javaLiteralOf((SingleValue) value);
    }

    if(value instanceof ListValue) {
      return javaLiteralOf(definition.type, (ListValue) value);
    }

    throw new IllegalStateException("Unsupported value type encountered");
  }

  private String javaLiteralOf(final SingleValue value) {
    return value.value().toString();
  }

  @SuppressWarnings("unchecked")
  private String javaLiteralOf(final Type type, final ListValue value) {
    return value.value().stream()
            .map(e -> ((SingleValue)e).value())
            .collect(joining(
                    ", ",
                    String.format("new %s { ", typeTypeOf(type)),
                    " }"
            )).toString();
  }

  private String typeTypeOf(final Type type) {
    if (type instanceof BasicType) {
      return primitive((BasicType) type);
    } else {
      return ((TypeDefinition) type).typeName;
    }
  }

  private Class<?> baseClassOf(final TypeDefinition type) {
    switch (type.category) {
      case Event:
        return DomainEvent.class;
      default:
        return DomainEvent.class;
    }
  }
}
