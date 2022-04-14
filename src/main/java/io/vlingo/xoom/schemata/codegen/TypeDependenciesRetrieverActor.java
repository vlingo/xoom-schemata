// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.model.Path;
import io.vlingo.xoom.schemata.query.CodeQueries;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeDependenciesRetrieverActor extends Actor implements TypeDependenciesRetriever {

  private final CodeQueries codeQueries;
  private final TypeDefinitionMiddleware middleware;

  public TypeDependenciesRetrieverActor(final CodeQueries codeQueries) {
    this.codeQueries = codeQueries;
    this.middleware = TypeDefinitionMiddleware.middlewareFor(stage());
  }

  @Override
  public Completes<TypeDependencies> dependenciesOf(final String rootReference) {
    return answerFrom(codeQueries.codeFor(Path.with(rootReference, true))
            .andThenTo(codeView -> {
              final InputStream spec = new ByteArrayInputStream(codeView.specification().getBytes());
              return middleware.compileToAST(spec, rootReference);
            }).andThen(outcome -> {
              final Set<String> schemaNames = outcome.resolve(
                      ex -> Collections.emptySet(),
                      node -> resolveComplexTypedSchemaNames((TypeDefinition) node)
              );
              final TypeDependencies typeDependencies = TypeDependencies.with(rootReference);
              typeDependencies.add(schemaNames);
              return typeDependencies;
            })
    );
  }

  private Set<String> resolveComplexTypedSchemaNames(final TypeDefinition typeDefinition) {
    return typeDefinition.children.stream()
            .map(field -> ((FieldDefinition) field).type)
            .filter(fieldType -> fieldType.isComplexType() || fieldType.containsComplexType())
            .map(fieldType -> fieldType.name().replace("[]", ""))
            .collect(Collectors.toSet());

  }

}
