package io.vlingo.xoom.schemata.codegen.ast.types;

import java.util.Objects;

public class ComputableType implements Type {
    public final String typeName;

    public ComputableType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComputableType that = (ComputableType) o;
        return Objects.equals(typeName, that.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName);
    }

    @Override
    public String name() {
        return typeName;
    }

    @Override
    public String toString() {
      return "ComputableType [typeName=" + typeName + "]";
    }
}
