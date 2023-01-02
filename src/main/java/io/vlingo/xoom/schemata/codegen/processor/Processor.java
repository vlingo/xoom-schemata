// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.processor;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.xoom.schemata.codegen.ast.Node;

public interface Processor {
    Completes<Node> process(final Node node, final TypeDefinitionMiddleware middleware, final String fullyQualifiedTypeName);

    @SuppressWarnings("unchecked")
    static <T extends Node> T requireBeing(Node node, Class<T> nodeClass) {
        if (nodeClass.isInstance(node)) {
            return (T) node;
        }

        final String expectedType = nodeClass.getCanonicalName();
        final String actualType = node == null ? "null" : node.getClass().getCanonicalName();

        throw new IllegalArgumentException(String.format("%s is not of type %s but type %s", node, expectedType, actualType));
    }
}
