// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.codegen.backend.Backend;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;

import java.io.InputStream;

public class BackendTypeDefinitionCompiler implements TypeDefinitionCompiler {
  private final TypeDefinitionMiddleware middleware;
  private final Backend backend;

  public BackendTypeDefinitionCompiler(final TypeDefinitionMiddleware middleware, final Backend backend) {
    this.middleware = middleware;
    this.backend = backend;
  }

  @Override
  public Completes<Outcome<SchemataBusinessException, String>> compile(final InputStream typeDefinition, final String fullyQualifiedTypeName, final String version) {
    return middleware.compileToAST(typeDefinition, fullyQualifiedTypeName)
            .andThen(outcome -> outcome.andThenTo(node -> generateOutput(node, version)));
  }

  private Outcome<SchemataBusinessException, String> generateOutput(final Node node, final String version) {
    return backend.generateOutput(node, version).otherwiseFail(ex -> ex);
  }
}
