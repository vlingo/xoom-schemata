package io.vlingo.schemata.codegen.ast.types;

public class ComputableType implements Type {
    public final String typeName;

    public ComputableType(String typeName) {
        this.typeName = typeName;
    }
}
