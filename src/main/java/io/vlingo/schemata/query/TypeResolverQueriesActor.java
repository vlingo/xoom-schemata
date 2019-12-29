// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;

import java.io.ByteArrayInputStream;
import java.util.Optional;

public class TypeResolverQueriesActor extends Actor implements TypeResolverQueries {
    private final SchemaVersionQueries schemaVersionQueries;

    public TypeResolverQueriesActor(final SchemaVersionQueries schemaVersionQueries) {
        this.schemaVersionQueries = schemaVersionQueries;
    }

    @Override
    public Completes<Optional<TypeDefinition>> resolve(TypeDefinitionMiddleware middleware, String fullyQualifiedTypeName) {
        return schemaVersionQueries
                .schemaVersion(fullyQualifiedTypeName)
                .andThenTo(data -> data == null ?
                        Completes.withSuccess(null) :
                        middleware.compileToAST(new ByteArrayInputStream(data.specification.getBytes()), fullyQualifiedTypeName))
                .andThen(node -> Optional.ofNullable((TypeDefinition) node))
                .otherwise(ex -> Optional.empty());
    }
}
