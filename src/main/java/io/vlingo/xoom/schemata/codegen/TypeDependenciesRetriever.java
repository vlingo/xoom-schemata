// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Path;
import io.vlingo.xoom.schemata.query.CodeQueries;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public interface TypeDependenciesRetriever {

  static TypeDependenciesRetriever with(final Stage stage, final CodeQueries codeQueries) {
    return stage.actorFor(TypeDependenciesRetriever.class, TypeDependenciesRetrieverActor.class, codeQueries);
  }

  Completes<TypeDependencies> dependenciesOf(final String reference);

  class TypeDependencies {
    public final Path rootReference;
    public final Set<String> dependencyReferences = new HashSet<>();

    public static TypeDependencies with(final String rootReference) {
      return new TypeDependencies(rootReference);
    }

    private TypeDependencies(final String rootReference) {
      this.rootReference = Path.with(rootReference, true);
    }

    public String add(final String schema) {
      final String dependencyReference = rootReference.withSchema(schema).toReference();
      this.dependencyReferences.add(rootReference.withSchema(schema).toReference());
      return dependencyReference;
    }

    public Iterator<String> add(final Set<String> schemaNames) {
      schemaNames.forEach(this::add);
      return this.dependencyReferences.iterator();
    }

  }

}
