package io.vlingo.schemata.codegen.backends.java.values;

import io.vlingo.common.Tuple2;

import java.util.Optional;

public final class IntrinsicField extends EventField {
    protected IntrinsicField(String ownerClass, String typeName, String fieldName) {
        super(ownerClass, typeName, fieldName);
    }

    @Override
    public Tuple2<Class<?>, String> fieldDefinition() {
        switch (typeName) {
            case "type":
                return Tuple2.from(String.class, fieldName);
            case "timestamp":
                return Tuple2.from(Long.class, fieldName);
            case "version":
                return Tuple2.from(Integer.class, fieldName);
        }

        throw new IllegalStateException("Unknown typeName " + typeName);
    }

    public static IntrinsicField of(String ownerClass, String typeName, String fieldName) {
        return new IntrinsicField(ownerClass, typeName, fieldName);
    }

    @Override
    public Optional<Tuple2<Class<?>, String>> parameterDefinition() {
        return Optional.empty();
    }

    @Override
    public String initialization() {
        switch (typeName) {
            case "type":
                return String.format("this.%s = \"%s\";", fieldName, ownerClass);
            case "timestamp":
                return String.format("this.%s = System.currentTimeMillis();", fieldName);
            case "version":
                return String.format("this.%s = SemanticVersion.toValue(0, 0, 1);", fieldName);
        }
        return "// " + typeName;
    }
}
