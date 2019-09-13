// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.processor.types.TypeResolver;

public class InMemoryTypeResolver implements TypeResolver {
    private final Map<String, TypeDefinition> types;

    public InMemoryTypeResolver() {
        types = new HashMap<>();
    }

    @Override
    public Optional<TypeDefinition> resolve(String fullQualifiedTypeName) {
        return Optional.ofNullable(types.get(fullQualifiedTypeName));
    }

    public void produce(TypeDefinition typeDefinition, String version) {
        types.put(typeDefinition.typeName, typeDefinition);
    }
}
