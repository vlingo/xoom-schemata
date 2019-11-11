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
import io.vlingo.schemata.codegen.ast.types.Type;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.processor.Processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class TypeResolverProcessor extends Actor implements Processor {
    private final TypeResolver resolver;

    public TypeResolverProcessor(TypeResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Completes<Node> process(Node node, final String fullyQualifiedTypeName) {
        TypeDefinition type = Processor.requireBeing(node, TypeDefinition.class);

        List<Completes<Node>> processedTypeList = type.children.stream()
                .map(e -> (FieldDefinition) e)
                .map(this::resolveType)
                .map(e -> (Completes) e)
                .map(e -> (Completes<Node>) e)
                .collect(Collectors.toList());

        Completes<List<Node>> eventuallyProcessedTypes = unwrap(processedTypeList);
        eventuallyProcessedTypes.andFinallyConsume(processedTypes -> {
            completesEventually().with(new TypeDefinition(type.category, fullyQualifiedTypeName, type.typeName, processedTypes));
        });

        return completes();

    }

    private Completes<FieldDefinition> resolveType(FieldDefinition fieldDefinition) {
      final Type typeNode = fieldDefinition.type;

      if (typeNode instanceof BasicType) {
          final BasicType basicType = (BasicType) typeNode;

          Completes<Type> resolvedType = resolver.resolve(basicType.typeName)
                  .andThen(foundType -> foundType.map(definition -> (Type) definition).orElse(basicType));

          return resolvedType.andThen(type ->
                  new FieldDefinition(type, fieldDefinition.version, fieldDefinition.name, fieldDefinition.defaultValue)
          );
      }

      return Completes.withSuccess(fieldDefinition);
    }

    private <T> Completes<List<T>> unwrap(List<Completes<T>> completes) {
        CountDownLatch latch = new CountDownLatch(completes.size());
        List<T> result = new ArrayList<>(completes.size());
        completes.forEach(complete -> {
            complete.andThenConsume(result::add)
                    .andFinallyConsume(e -> latch.countDown());
        });

        return Completes.withSuccess(result)
                .andThenConsume(i -> {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        logger().error("TypeResolverProcessor could not unwrap list of Completes<T> " + e.getMessage(), e);
                    }
                });
    }
}
