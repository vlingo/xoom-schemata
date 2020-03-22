package io.vlingo.schemata.codegen;

import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.errors.SchemataBusinessException;

import java.io.InputStream;

public interface TypeDefinitionMiddleware {
    Completes<Outcome<SchemataBusinessException,Node>> compileToAST(final InputStream typeDefinition, final String fullyQualifiedTypeName);

    public static TypeDefinitionMiddleware middlewareFor(final Stage stage) {
        //TODO: factor out Middleware from compiler actor to be able to retrieve it w/o language
        return TypeDefinitionCompiler.compilerFor(stage,"java").middleware();
    }
}
