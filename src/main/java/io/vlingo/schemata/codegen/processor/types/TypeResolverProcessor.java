// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.processor.types;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.schemata.codegen.ast.FieldDefinition;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.ast.types.BasicType;
import io.vlingo.schemata.codegen.ast.types.Type;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.processor.Processor;

public class TypeResolverProcessor extends Actor implements Processor {
    private final TypeResolver resolver;

    public TypeResolverProcessor(TypeResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Completes<Node> process(final Node node, final TypeDefinitionMiddleware middleware, final String fullyQualifiedTypeName) {
        TypeDefinition type = Processor.requireBeing(node, TypeDefinition.class);

        List<Completes<Node>> processedTypeList = type.children.stream()
                .map(e -> (FieldDefinition) e)
                .map(fieldDefinition -> this.resolveType(middleware, fieldDefinition))
                .map(e -> (Completes) e)
                .map(e -> (Completes<Node>) e)
                .collect(Collectors.toList());

        Completes<List<Node>> eventuallyProcessedTypes = unwrap(processedTypeList);
        eventuallyProcessedTypes.andFinallyConsume(processedTypes -> {
            completesEventually().with(new TypeDefinition(type.category, fullyQualifiedTypeName, type.typeName, processedTypes));
        });

        return completes();

    }

    private Completes<FieldDefinition> resolveType(final TypeDefinitionMiddleware middleware, final FieldDefinition fieldDefinition) {
      final Type typeNode = fieldDefinition.type;

      if (typeNode instanceof BasicType) {
          final BasicType basicType = (BasicType) typeNode;

          Completes<Type> resolvedType = resolver.resolve(middleware, basicType.typeName)
                  .andThen(foundType -> foundType.map(definition -> (Type) definition).orElse(basicType));

          // FIXME: add array type info from field definition
          return resolvedType.andThen(type ->
                  new FieldDefinition(type, fieldDefinition.version, fieldDefinition.name, fieldDefinition.defaultValue)
          );
      }

      return Completes.withSuccess(fieldDefinition);
    }

    private <T> Completes<List<T>> unwrap(List<Completes<T>> completes) {
        final List<T> result = new ArrayList<>(completes.size());
        completes.forEach(complete -> result.add(complete.await()));

        return Completes.withSuccess(result);
    }
}
