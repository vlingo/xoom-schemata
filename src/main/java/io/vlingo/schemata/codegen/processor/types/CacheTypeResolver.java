// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.processor.types;

import io.vlingo.common.Completes;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CacheTypeResolver implements TypeResolver {
    private final Map<String, TypeDefinition> types;

    public CacheTypeResolver() {
        types = new HashMap<>();
    }

    @Override
    public Completes<Optional<TypeDefinition>> resolve(String fullQualifiedTypeName) {
        for (final TypeDefinition type : types.values()) {
          if (type.fullyQualifiedTypeName.equals(fullQualifiedTypeName)) {
            return Completes.withSuccess(Optional.of(type));
          }
        }
        return Completes.withSuccess(Optional.empty());
    }

    public void produce(TypeDefinition typeDefinition, String version) {
        types.put(typeDefinition.typeName, typeDefinition);
    }
}
