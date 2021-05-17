package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.types.BasicArrayType;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.ast.values.ListValue;
import io.vlingo.xoom.schemata.codegen.ast.values.SingleValue;
import io.vlingo.xoom.schemata.codegen.ast.values.Value;
import io.vlingo.xoom.schemata.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class SchemaTypeArguments {

    public final String namespace;
    public final String version;
    public final List<Property> properties;
    public final Category type;
    public final String typeName;
    public final boolean createDefaultConstructor;
    public final boolean createRequiredArgsConstructor;

    public SchemaTypeArguments(String language, String fqdn, String version, TypeDefinition node) {
        this.namespace = extractNamespace(fqdn);
        this.type = node.category;
        this.typeName = node.typeName;
        this.version = version;
        this.properties = extractProperties(language, node);
        this.createDefaultConstructor = properties.stream()
                .filter(property -> !property.name.equals("eventType"))
                .filter(property -> !property.name.equals("eventVersion"))
                .filter(property -> !property.name.equals("occurredOn"))
                .allMatch(property -> property.value != null);
        this.createRequiredArgsConstructor = properties.stream()
                .anyMatch(property -> !property.name.equals("eventType")
                                        && !property.name.equals("eventVersion")
                                        && !property.name.equals("occurredOn"));
    }

    public static class Property {
        public final String type;
        public final boolean array;
        public final String name;
        public final Object value;

        public Property(String type, boolean array, String name, Object value) {
            this.type = type;
            this.array = array;
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Property{" +
                    "type='" + type + '\'' +
                    ", array=" + array +
                    ", name='" + name + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

    private String extractNamespace(String fqdn) {
        int i = fqdn.indexOf(':', fqdn.indexOf(':') + 1) + 1;
        int j = fqdn.indexOf(':', i);
        return fqdn.substring(i, j) + ".events";
    }

    private List<Property> extractProperties(String language, TypeDefinition root) {
        TypeMap typeMap = TypeMap.valueOf(language);
        return root.children.stream()
                .map(node -> (FieldDefinition) node)
                .map(node -> {
                    String type = typeMap.typeOf(node.type.name());
                    boolean array = node.type instanceof BasicArrayType;
                    Object defaultValue = array
                            ? node.defaultValue
                                    .filter(value -> value instanceof ListValue)
                                    .map(value -> (ListValue<List<SingleValue>>)value)
                                    .map(listValue -> listValue.value.stream()
                                                                    .map(Value::value)
                                                                    .collect(Collectors.toList()))
                                    .orElse(null)
                            : node.defaultValue.map(Value::value).orElse(null);
                    return new Property(type, array, node.name, defaultValue);
                })
                .collect(Collectors.toList());
    }
}
