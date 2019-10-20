// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.processor.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.vlingo.schemata.codegen.ast.types.TypeDefinition;

public class CacheTypeResolver implements TypeResolver {
    private final Map<String, TypeDefinition> types;

    public CacheTypeResolver() {
        types = new HashMap<>();
    }

    @Override
    public Optional<TypeDefinition> resolve(String fullQualifiedTypeName, final String simpleTypeName) {
        for (final TypeDefinition type : types.values()) {
          if (type.fullyQualifiedTypeName.equals(fullQualifiedTypeName) || type.typeName.equals(simpleTypeName)) {
            return Optional.of(type);
          }
        }
        return Optional.empty();
    }

    public void produce(TypeDefinition typeDefinition, String version) {
        types.put(typeDefinition.typeName, typeDefinition);
    }
}
