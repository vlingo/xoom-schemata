package io.vlingo.xoom.schemata.codegen.ast.types;

import io.vlingo.xoom.schemata.model.Category;

import java.util.Objects;

public class ComplexType implements Type {
  public final Category category;
  public final String typeName;

  public ComplexType(Category category, String typeName) {
    this.category = category;
    this.typeName = typeName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ComplexType complexType = (ComplexType) o;
    return category == complexType.category && Objects.equals(typeName, complexType.typeName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, typeName);
  }

  @Override
  public String name() {
    return typeName;
  }

  public boolean isArrayType() {
    return false;
  }

  @Override
  public String toString() {
    return "ComplexType [category=" + category + ", typeName=" + typeName + "]";
  }
}
