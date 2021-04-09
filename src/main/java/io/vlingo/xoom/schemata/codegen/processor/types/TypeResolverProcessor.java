// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.processor.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.codegen.ast.types.BasicType;
import io.vlingo.xoom.schemata.codegen.ast.types.Type;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.processor.Processor;

public class TypeResolverProcessor extends Actor implements Processor {
    private final TypeResolver resolver;

    public TypeResolverProcessor(TypeResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Completes<Node> process(final Node node, final TypeDefinitionMiddleware middleware, final String fullyQualifiedTypeName) {
        TypeDefinition type = Processor.requireBeing(node, TypeDefinition.class);

        List<Node> processedTypeList = type.children.stream()
                .map(e -> (FieldDefinition) e)
                .map(fieldDefinition -> this.resolveType(middleware, fieldDefinition))
                .collect(Collectors.toList());

        completesEventually().with(new TypeDefinition(type.category, fullyQualifiedTypeName, type.typeName, processedTypeList));

        return completes();

    }

    private FieldDefinition resolveType(final TypeDefinitionMiddleware middleware, final FieldDefinition fieldDefinition) {
      final Type typeNode = fieldDefinition.type;

      if (typeNode instanceof BasicType) {
          final BasicType basicType = (BasicType) typeNode;

          Optional<TypeDefinition> maybeResolvedType = resolver.resolve(middleware, basicType.typeName).outcome();

          // outcome() + null check is a temp. workaround for cases in which the resolver would hang, will be removed
          // once the type resolver is fixed.
          Type resolvedType = maybeResolvedType == null ? basicType : maybeResolvedType
                  .map(definition -> (Type) definition)
                  .orElse(basicType);

          // FIXME: add array type info from field definition
          return new FieldDefinition(resolvedType, fieldDefinition.version, fieldDefinition.name, fieldDefinition.defaultValue);
      }

      return fieldDefinition;
    }

    @SuppressWarnings({"unused"})
    private <T> Completes<List<T>> unwrap(List<Completes<T>> completes) {
        final List<T> result = new ArrayList<>(completes.size());
        completes.forEach(complete -> result.add(complete.await()));

        return Completes.withSuccess(result);
    }
}
