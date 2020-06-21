// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.errors.SchemataBusinessException;
import io.vlingo.schemata.model.Path;

public class TypeResolverQueriesActor extends Actor implements TypeResolverQueries {
    private final CodeQueries codeQueries;

    public TypeResolverQueriesActor(final CodeQueries codeQueries) {
        this.codeQueries = codeQueries;
    }

    @Override
    public Completes<Optional<TypeDefinition>> resolve(TypeDefinitionMiddleware middleware, String fullyQualifiedTypeName) {
        if (Path.isValidReference(fullyQualifiedTypeName, true)) {
            final Path path = Path.with(fullyQualifiedTypeName, true);
            return codeQueries.codeFor(path)
                    .andThen(codeView -> {
                        final ByteArrayInputStream typeDefinition = new ByteArrayInputStream(codeView.specification().getBytes());
                        return middleware.compileToAST(typeDefinition, fullyQualifiedTypeName).<Outcome<SchemataBusinessException, Node>>await();
                    })
                    .andThen(outcome -> outcome.resolve(ex -> null, node -> node))
                    .andThen(node -> Optional.ofNullable((TypeDefinition) node))
                    .otherwise(ex -> Optional.empty());
        } else {
            return completes().with(Optional.empty());
        }
    }
}
