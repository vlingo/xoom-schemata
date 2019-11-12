package io.vlingo.schemata.codegen;

import io.vlingo.common.Completes;
import io.vlingo.schemata.codegen.ast.Node;

import java.io.InputStream;

public interface TypeDefinitionMiddleware {
    Completes<Node> compileToAST(final InputStream typeDefinition, final String fullyQualifiedTypeName);
}
