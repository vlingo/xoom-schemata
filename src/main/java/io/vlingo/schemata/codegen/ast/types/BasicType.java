package io.vlingo.schemata.codegen.ast.types;

public class BasicType implements Type {
    public final String typeName;

    public BasicType(String typeName) {
        this.typeName = typeName;
    }
}
