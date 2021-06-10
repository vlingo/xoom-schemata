package io.vlingo.xoom.schemata.codegen.template.schematype.csharp;

import io.vlingo.xoom.schemata.codegen.ast.types.*;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypePackage;

import java.util.Arrays;
import java.util.List;

public class CSharpType {
  private final Type type;
  private final TypeDefinition owner;

  private CSharpType(final Type type, final TypeDefinition owner) {
    this.type = type;
    this.owner = owner;
  }

  public static CSharpType from(final Type type, final TypeDefinition owner) {
    return new CSharpType(type, owner);
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

  public List<String> namespaceImports() {
    if (type instanceof ArrayType) {
      return CSharpType.from(((ArrayType) type).elementType, owner).namespaceImports();
    }
    if (type instanceof ComplexType) {
      return Arrays.asList(
              SchemaTypePackage.from(owner.fullyQualifiedTypeName, ((ComplexType) type).category.name(), ".")
                      .withTitleCaseSegmentFormatter()
                      .name()
      );
    }
    return Arrays.asList();
  }

  private String array(final ArrayType type) {
    return CSharpType.from(type.elementType, owner).simpleName() + "[]";
  }

  private String primitive(final BasicType basicType) {
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

  private String computable(final ComputableType computableType) {
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

  private String complex(final ComplexType complexType) {
    return complexType.name();
  }
}
