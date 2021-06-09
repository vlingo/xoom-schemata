package io.vlingo.xoom.schemata.codegen.template.schematype.java;

import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.lattice.model.DomainEvent;
import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.types.*;
import io.vlingo.xoom.schemata.codegen.ast.values.ListValue;
import io.vlingo.xoom.schemata.codegen.ast.values.NullValue;
import io.vlingo.xoom.schemata.codegen.ast.values.SingleValue;
import io.vlingo.xoom.schemata.codegen.ast.values.Value;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypeTemplateData;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

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
    List<Field> fields = fields();
    return TemplateParameters
            .with(JavaSchemaTypeTemplateParameter.PACKAGE, packageName())
            .and(JavaSchemaTypeTemplateParameter.TYPE_NAME, typeName())
            .and(JavaSchemaTypeTemplateParameter.BASE_TYPE_NAME, baseTypeName())
            .and(JavaSchemaTypeTemplateParameter.NEEDS_CONSTRUCTOR, needsConstructor(fields))
            .and(JavaSchemaTypeTemplateParameter.NEEDS_DEFAULT_CONSTRUCTOR, needsDefaultConstructor(fields))
            .and(JavaSchemaTypeTemplateParameter.FIELDS, fields)
            .and(JavaSchemaTypeTemplateParameter.COMPUTED_FIELDS, computedFields(fields))
            .and(JavaSchemaTypeTemplateParameter.IMPORTS, imports());
  }

  private List<Field> fields() {
    return type.children.stream()
            .filter(c -> c instanceof FieldDefinition)
            .map(c -> (FieldDefinition) c)
            .map(this::toField)
            .collect(Collectors.toList());
  }

  private List<Field> computedFields(final List<Field> fields) {
    return fields.stream().filter(f -> f.isComputed).collect(Collectors.toList());
  }

  private String packageName() {
    return packageSegments(type.fullyQualifiedTypeName, type.category.name().toLowerCase()).stream().collect(joining("."));
  }

  private String typeName() {
    return type.typeName;
  }

  private String baseTypeName() {
    return baseClass().getSimpleName();
  }

  private String basePackageName() {
    return baseClass().getName();
  }

  private Class<?> baseClass() {
    switch (type.category) {
      case Event:
        return DomainEvent.class;
      default:
        return DomainEvent.class;
    }
  }

  private boolean needsConstructor(final List<Field> fields) {
    return fields.stream().anyMatch(f -> !f.isComputed);
  }

  private boolean needsDefaultConstructor(final List<Field> fields) {
    return fields.size() != 0 && fields.stream().allMatch(f -> f.isComputed || f.defaultValue != null);
  }

  private List<String> imports() {
    return Stream.concat(Stream.of(basePackageName()), fieldTypes().stream()).sorted().collect(Collectors.toList());
  }

  private Set<String> fieldTypes() {
    return fields().stream()
            .map(f -> {
              if (f.type.equals("String") || f.type.equals("String[]")) {
                return "java.lang.String";
              }
              return null;
            })
            .filter(f -> f != null)
            .collect(Collectors.toSet());
  }

  private String type(final Type type) {
    if (type instanceof ArrayType) {
      return type(((ArrayType) type).elementType) + "[]";
    } else if (type instanceof BasicType) {
      return primitive((BasicType) type);
    } else if (type instanceof ComputableType) {
      return computable((ComputableType) type);
    }
    return type.name();
  }

  private String primitive(final BasicType basicType) {
    switch (basicType.typeName) {
      case "boolean":
      case "byte":
      case "char":
      case "short":
      case "int":
      case "long":
      case "float":
      case "double":
        return basicType.typeName;
      case "string":
        return "String";
      default:
        return "Object";
    }
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
                    String.format("new %s { ", type(type)),
                    " }"
            )).toString();
  }

  private String initializationOf(final FieldDefinition definition, final TypeDefinition owner) {
    Type type = definition.type;
    if (type instanceof ComputableType) {
      switch (((ComputableType) type).typeName) {
        case "type":
          return String.format("\"%s\"", owner.typeName);
        case "version":
          return String.format("io.vlingo.xoom.common.version.SemanticVersion.toValue(\"%s\")", this.version);
        case "timestamp":
          return "System.currentTimeMillis()";
      }
    }

    return definition.name;
  }

  private Field toField(final FieldDefinition field) {
    return new Field(
            type(field.type),
            field.name,
            javaLiteralOf(field),
            initializationOf(field, type),
            field.type instanceof ComputableType
    );
  }

  public class Field {
    public final String type;
    public final String name;
    public final String defaultValue;
    public final String constructorInitializer;
    public final boolean isComputed;

    public Field(final String type, final String name, final String defaultValue, final String constructorInitializer, final boolean isComputed) {
      this.type = type;
      this.name = name;
      this.defaultValue = defaultValue;
      this.constructorInitializer = constructorInitializer;
      this.isComputed = isComputed;
    }
  }
}
