package io.vlingo.xoom.schemata.codegen.ast.types;

import java.util.Objects;

public class BasicType implements Type {
    public final String typeName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicType basicType = (BasicType) o;
        return Objects.equals(typeName, basicType.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName);
    }

    public BasicType(String typeName) {
        this.typeName = typeName;
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
      return "BasicType [typeName=" + typeName + "]";
    }
}
