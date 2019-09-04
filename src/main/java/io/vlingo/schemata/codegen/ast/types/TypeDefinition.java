package io.vlingo.schemata.codegen.ast.types;

import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.model.Category;

import java.util.List;

public class TypeDefinition implements Node, Type {
    public final Category category;
    public final String typeName;
    public final List<Node> children;

    public TypeDefinition(Category category, String typeName, List<Node> children) {
        this.category = category;
        this.typeName = typeName;
        this.children = children;
    }
}
