package io.vlingo.xoom.schemata.codegen.template.schematype.csharp;

import static java.util.stream.Collectors.joining;

import java.util.List;

import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.types.ComputableType;
import io.vlingo.xoom.schemata.codegen.ast.types.Type;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.ast.values.ListValue;
import io.vlingo.xoom.schemata.codegen.ast.values.NullValue;
import io.vlingo.xoom.schemata.codegen.ast.values.SingleValue;
import io.vlingo.xoom.schemata.codegen.ast.values.Value;

public class CSharpProperty {
  public final String type;
  public final List<String> namespaceImports;
  public final String name;
  public final String argumentName;
  public final String defaultValue;
  public final String constructorInitializer;
  public final boolean isComputed;

  private CSharpProperty(final CSharpType type, final String name, final String argumentName, final String defaultValue, final String constructorInitializer, final boolean isComputed) {
    this.type = type.simpleName();
    this.namespaceImports = type.namespaceImports();
    this.name = name;
    this.argumentName = argumentName;
    this.defaultValue = defaultValue;
    this.constructorInitializer = constructorInitializer;
    this.isComputed = isComputed;
  }

  public static CSharpProperty from(final FieldDefinition field, final TypeDefinition owner, final String version) {
    return new CSharpProperty(
            CSharpType.from(field.type, owner),
            fieldName(field.name),
            argumentName(field.name),
            cSharpLiteralOf(field),
            initializationOf(field, owner, version),
            field.type instanceof ComputableType
    );
  }

  private static String fieldName(final String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  private static String argumentName(final String name) {
    return name.substring(0, 1).toLowerCase() + name.substring(1);
  }

  private static String initializationOf(final FieldDefinition field, final TypeDefinition owner, final String version) {
    Type type = field.type;
    if (type instanceof ComputableType) {
      switch (((ComputableType) type).typeName) {
        case "type":
          return String.format("\"%s\"", owner.typeName);
        case "version":
          return String.format("SemanticVersion.toValue(\"%s\")", version);
        case "timestamp":
          return "DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()";
      }
    }
    return field.name;
  }

  private static String cSharpLiteralOf(final FieldDefinition field) {
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

  private static String cSharpLiteralOf(final SingleValue<?> value) {
    return value.value().toString();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static String cSharpLiteralOf(final ListValue value) {
    return value.value().stream()
            .map(e -> ((SingleValue<?>) e).value())
            .collect(joining(", ", "{ ", " }"))
            .toString();
  }
}
