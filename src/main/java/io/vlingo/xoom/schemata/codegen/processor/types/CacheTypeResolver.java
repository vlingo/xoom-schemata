// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.processor.types;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CacheTypeResolver implements TypeResolver {
    private final Set<TypeDefinition> types;

    public CacheTypeResolver() {
        types = new HashSet<>();
    }

    @Override
    public Completes<Optional<TypeDefinition>> resolve(final TypeDefinitionMiddleware middleware, final String fullyQualifiedTypeName) {
        for (final TypeDefinition type : types) {
          if (doesTypeMatch(fullyQualifiedTypeName, type.fullyQualifiedTypeName)) {
            return Completes.withSuccess(Optional.of(type));
          }
        }
        return Completes.withSuccess(Optional.empty());
    }

    public void produce(TypeDefinition typeDefinition, String version) {
        types.add(typeDefinition);
    }

    private boolean doesTypeMatch(String query, String fqn) {
        if (hasVersion(query)) {
            String queryWithoutVersion = query.substring(0, query.lastIndexOf(":"));
            return queryWithoutVersion.equals(fqn);
        }

        return query.equals(fqn);
    }

    private boolean hasVersion(String query) {
        return (query.length() - query.replace(":", "").length()) > 4;
    }
}
