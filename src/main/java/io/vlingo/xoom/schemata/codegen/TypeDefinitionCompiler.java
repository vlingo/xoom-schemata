// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.schemata.codegen.backend.XoomCodeGenBackend;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypeTemplateProcessingStep;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;

import java.io.InputStream;

/**
 * A compiler of schema types, producing source code for a given language.
 */
public interface TypeDefinitionCompiler {

  /**
   * Answer the eventually generated source code given the {@code typeDefinition} and {@code version}.
   *
   * @param typeDefinition         the InputStream providing the schema type definition
   * @param fullyQualifiedTypeName the String FQTN of the type to be compiled
   * @param version                the String version of the definition
   * @return {@code Completes<String>}
   */
  Completes<Outcome<SchemataBusinessException, String>> compile(final InputStream typeDefinition, final String fullyQualifiedTypeName, final String version);

  static TypeDefinitionCompiler compilerFor(final Stage stage, final String language) {
    return new BackendTypeDefinitionCompiler(
            TypeDefinitionMiddleware.middlewareFor(stage),
            new XoomCodeGenBackend(new SchemaTypeTemplateProcessingStep(), language)
    );
  }
}
