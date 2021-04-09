package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;

import java.io.InputStream;

public interface TypeDefinitionMiddleware {
    Completes<Outcome<SchemataBusinessException,Node>> compileToAST(final InputStream typeDefinition, final String fullyQualifiedTypeName);

    public static TypeDefinitionMiddleware middlewareFor(final Stage stage) {
        //TODO: factor out Middleware from compiler actor to be able to retrieve it w/o language
        return TypeDefinitionCompiler.compilerFor(stage,"java").middleware();
    }
}
