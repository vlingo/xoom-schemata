// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.processor.types;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.codegen.ast.types.BasicType;
import io.vlingo.xoom.schemata.codegen.ast.types.ComputableType;
import io.vlingo.xoom.schemata.codegen.ast.types.Type;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.processor.Processor;

import java.util.List;
import java.util.stream.Collectors;

public class ComputableTypeProcessor extends Actor implements Processor {
    @Override
    public Completes<Node> process(final Node node, final TypeDefinitionMiddleware middleware, final String fullyQualifiedTypeName) {
        TypeDefinition type = Processor.requireBeing(node, TypeDefinition.class);

        List<Node> processedTypes = type.children.stream()
                .map(e -> (FieldDefinition) e)
                .map(this::resolveType)
                .collect(Collectors.toList());

        completesEventually().with(new TypeDefinition(type.category, fullyQualifiedTypeName, type.typeName, processedTypes));
        return completes();

    }

    private FieldDefinition resolveType(FieldDefinition fieldDefinition) {
        Type typeNode = fieldDefinition.type;

        if (typeNode instanceof BasicType) {
            BasicType basicType = (BasicType) typeNode;
            switch (basicType.typeName) {
                case "version":
                case "timestamp":
                case "type":
                    return new FieldDefinition(new ComputableType(basicType.typeName), fieldDefinition.version, fieldDefinition.name, fieldDefinition.defaultValue);
            }
        }

        return fieldDefinition;
    }
}
