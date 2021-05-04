package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.ast.values.Value;

import java.util.List;
import java.util.stream.Collectors;

public class DomainEventArguments {

    public final String namespace;
    public final String version;
    public final List<Property> properties;
    public final String typeName;

    public DomainEventArguments(String language, String fqdn, String version, TypeDefinition node) {
        this.namespace = extractNamespace(fqdn);
        this.typeName = node.typeName;
        this.version = version;
        this.properties = extractProperties(language, node);
    }

    public static class Property {
        public final String type;
        public final String name;
        public final Object value;

        public Property(String type, String name, Object value) {
            this.type = type;
            this.name = name;
            this.value = value;
        }
    }

    private String extractNamespace(String fqdn) {
        int i = fqdn.indexOf(':', fqdn.indexOf(':') + 1) + 1;
        int j = fqdn.indexOf(':', i);
        return fqdn.substring(i, j) + ".model";
    }

    private List<Property> extractProperties(String language, TypeDefinition root) {
        TypeMap typeMap = TypeMap.valueOf(language);
        return root.children.stream()
                .map(node -> (FieldDefinition) node)
                .map(node -> new Property(typeMap.typeOf(node.type.name()), node.name,
                                                        node.defaultValue.map(Value::value).orElse(null)))
                .collect(Collectors.toList());
    }
}
