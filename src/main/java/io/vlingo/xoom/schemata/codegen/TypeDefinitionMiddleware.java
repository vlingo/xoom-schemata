package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.codegen.parser.AntlrTypeParser;
import io.vlingo.xoom.schemata.codegen.parser.TypeParser;
import io.vlingo.xoom.schemata.codegen.processor.Processor;
import io.vlingo.xoom.schemata.codegen.processor.types.ComputableTypeProcessor;
import io.vlingo.xoom.schemata.codegen.processor.types.TypeResolver;
import io.vlingo.xoom.schemata.codegen.processor.types.TypeResolverProcessor;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;

import java.io.InputStream;
import java.util.Arrays;

public interface TypeDefinitionMiddleware {
  Completes<Outcome<SchemataBusinessException, Node>> compileToAST(final InputStream typeDefinition, final String fullyQualifiedTypeName);

  static TypeDefinitionMiddleware middlewareFor(final Stage stage) {
    final TypeParser typeParser = new AntlrTypeParser();
    final TypeResolver typeResolver = StorageProvider.instance().typeResolverQueries;

    return new ParserTypeDefinitionMiddleware(typeParser,
            Arrays.asList(
                    stage.actorFor(Processor.class, ComputableTypeProcessor.class),
                    stage.actorFor(Processor.class, TypeResolverProcessor.class, typeResolver)
            ));
  }
}
