// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.codegen.parser.TypeParser;
import io.vlingo.xoom.schemata.codegen.processor.Processor;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

public class ParserTypeDefinitionMiddleware implements TypeDefinitionMiddleware {

  private final TypeParser parser;
  private final List<Processor> processors;

  public ParserTypeDefinitionMiddleware(final TypeParser parser, final List<Processor> processors) {
    this.parser = parser;
    this.processors = processors;
  }

  @Override
  public Completes<Outcome<SchemataBusinessException, Node>> compileToAST(InputStream typeDefinition, String fullyQualifiedTypeName) {
    return Completes.withSuccess(
            parser.parseTypeDefinition(typeDefinition, fullyQualifiedTypeName)
                    .andThen(this.process(fullyQualifiedTypeName))
                    .otherwiseFail(ex -> ex)
    );
  }

  private Function<Node, Node> process(final String fullyQualifiedTypeName) {
    return node -> {
      Completes<Node> result = Completes.withSuccess(node);
      for (Processor p : processors) {
        result = result.andThenTo(n -> p.process(n, this, fullyQualifiedTypeName));
      }
      return result.await();
    };
  }
}
