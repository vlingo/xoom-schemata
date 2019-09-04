package io.vlingo.schemata.codegen;

import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.processor.types.TypeResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryTypeResolver implements TypeResolver {
    private final Map<String, TypeDefinition> types;

    public InMemoryTypeResolver() {
        types = new HashMap<>();
    }

    @Override
    public Optional<TypeDefinition> resolve(String fullQualifiedTypeName) {
        return Optional.ofNullable(types.get(fullQualifiedTypeName));
    }

    public void produce(TypeDefinition typeDefinition, String version) {
        types.put(typeDefinition.category.name().toLowerCase() + "." + typeDefinition.typeName + ":" + version, typeDefinition);
    }
}
