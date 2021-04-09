// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.processor.types;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;

import java.util.Optional;

public interface TypeResolver {
    Completes<Optional<TypeDefinition>> resolve(final TypeDefinitionMiddleware middleware, final String fullyQualifiedTypeName);
}
