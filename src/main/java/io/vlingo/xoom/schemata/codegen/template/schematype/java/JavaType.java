package io.vlingo.xoom.schemata.codegen.template.schematype.java;

import io.vlingo.xoom.schemata.codegen.ast.types.*;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypePackage;

import java.util.Arrays;
import java.util.List;

public class JavaType {
  private final Type type;
  private final TypeDefinition owner;

  private JavaType(final Type type, final TypeDefinition owner) {
    this.type = type;
    this.owner = owner;
  }

  public static JavaType from(final Type type, final TypeDefinition owner) {
    return new JavaType(type, owner);
  }

  public String simpleName() {
    if (type instanceof ArrayType) {
      return array((ArrayType) type);
    } else if (type instanceof BasicType) {
      return primitive((BasicType) type);
    } else if (type instanceof ComputableType) {
      return computable((ComputableType) type);
    } else if (type instanceof ComplexType) {
      return complex((ComplexType) type);
    }
    return type.name();
  }

  public List<String> imports() {
    if (type instanceof ArrayType) {
      return JavaType.from(((ArrayType) type).elementType, owner).imports();
    }
    if (type instanceof ComplexType) {
      return Arrays.asList(SchemaTypePackage.from(owner.fullyQualifiedTypeName, ((ComplexType) type).category.name().toLowerCase(), ".").name() + "." + type.name());
    }
    if (type.name().equals("string") || type.name().equals("string[]") || type.name().equals("type")) {
      return Arrays.asList("java.lang.String");
    }
    if (type.name().equals("version")) {
      return Arrays.asList("io.vlingo.xoom.common.version.SemanticVersion");
    }
    return Arrays.asList();
  }

  private String array(final ArrayType type) {
    return JavaType.from(type.elementType, owner).simpleName() + "[]";
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

  private String complex(final ComplexType complexType) {
    return complexType.name();
  }
}
