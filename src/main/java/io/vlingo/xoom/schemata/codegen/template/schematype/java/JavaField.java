package io.vlingo.xoom.schemata.codegen.template.schematype.java;

import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.types.ComputableType;
import io.vlingo.xoom.schemata.codegen.ast.types.Type;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.ast.values.ListValue;
import io.vlingo.xoom.schemata.codegen.ast.values.NullValue;
import io.vlingo.xoom.schemata.codegen.ast.values.SingleValue;
import io.vlingo.xoom.schemata.codegen.ast.values.Value;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class JavaField {
  public final String type;
  public final List<String> typeImports;
  public final String name;
  public final String defaultValue;
  public final String constructorInitializer;
  public final boolean isComputed;

  private JavaField(final JavaType type, final String name, final String defaultValue, final String constructorInitializer, final boolean isComputed) {
    this.type = type.simpleName();
    this.typeImports = type.imports();
    this.name = name;
    this.defaultValue = defaultValue;
    this.constructorInitializer = constructorInitializer;
    this.isComputed = isComputed;
  }

  public static JavaField from(final FieldDefinition field, final TypeDefinition owner, final String version) {
    return new JavaField(
            JavaType.from(field.type, owner),
            field.name,
            javaLiteralOf(field, owner),
            initializationOf(field, owner, version),
            field.type instanceof ComputableType
    );
  }

  private static String javaLiteralOf(final FieldDefinition definition, final TypeDefinition owner) {
    Value value = definition.defaultValue.orElseGet(NullValue::new);

    if(value instanceof NullValue) {
      return null;
    }

    if(value instanceof SingleValue) {
      return javaLiteralOf((SingleValue) value);
    }

    if(value instanceof ListValue) {
      return javaLiteralOf(definition.type, owner, (ListValue) value);
    }

    throw new IllegalStateException("Unsupported value type encountered");
  }

  private static String javaLiteralOf(final SingleValue value) {
    return value.value().toString();
  }

  @SuppressWarnings("unchecked")
  private static String javaLiteralOf(final Type type, final TypeDefinition owner, final ListValue value) {
    return value.value().stream()
            .map(e -> ((SingleValue)e).value())
            .collect(joining(
                    ", ",
                    String.format("new %s { ", JavaType.from(type, owner).simpleName()),
                    " }"
            )).toString();
  }

  private static String initializationOf(final FieldDefinition definition, final TypeDefinition owner, final String version) {
    Type type = definition.type;
    if (type instanceof ComputableType) {
      switch (((ComputableType) type).typeName) {
        case "type":
          return String.format("\"%s\"", owner.typeName);
        case "version":
          return String.format("SemanticVersion.toValue(\"%s\")", version);
        case "timestamp":
          return "System.currentTimeMillis()";
      }
    }

    return definition.name;
  }
}
