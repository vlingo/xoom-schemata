// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.processor.types;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.schemata.codegen.ast.FieldDefinition;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.ast.types.BasicType;
import io.vlingo.schemata.codegen.ast.types.ComputableType;
import io.vlingo.schemata.codegen.ast.types.Type;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.processor.Processor;

import java.util.List;
import java.util.stream.Collectors;

public class ComputableTypeProcessor extends Actor implements Processor {
    @Override
    public Completes<Node> process(Node node) {
        TypeDefinition type = Processor.requireBeing(node, TypeDefinition.class);

        List<Node> processedTypes = type.children.stream()
                .map(e -> (FieldDefinition) e)
                .map(this::resolveType)
                .collect(Collectors.toList());

        completesEventually().with(new TypeDefinition(type.category, type.typeName, processedTypes));
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
