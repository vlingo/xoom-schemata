package io.vlingo.schemata.codegen.processor.types;

import io.vlingo.schemata.codegen.ast.types.TypeDefinition;

import java.util.Optional;

public interface TypeResolver {
    Optional<TypeDefinition> resolve(String fullQualifiedTypeName);
}
