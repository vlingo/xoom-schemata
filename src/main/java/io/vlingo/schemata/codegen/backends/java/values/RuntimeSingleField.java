package io.vlingo.schemata.codegen.backends.java.values;

import io.vlingo.common.Tuple2;

import java.util.Optional;

public final class RuntimeSingleField extends EventField {
    private final String defaultValue;

    protected RuntimeSingleField(String ownerClass, String typeName, String fieldName, String defaultValue) {
        super(ownerClass, typeName, fieldName);
        this.defaultValue = defaultValue;
    }

    public static EventField of(String ownerClass, String typeName, String fieldName, String defaultValue) {
        return new RuntimeSingleField(ownerClass, typeName, fieldName, defaultValue);
    }

    @Override
    public Tuple2<Class<?>, String> fieldDefinition() {
        return Tuple2.from(JavaTypeDictionary.typeOf(typeName), fieldName);
    }

    @Override
    public Optional<Tuple2<Class<?>, String>> parameterDefinition() {
        if (defaultValue == null) {
            return Optional.of(Tuple2.from(JavaTypeDictionary.typeOf(typeName), fieldName));
        }

        return Optional.empty();
    }

    @Override
    public String initialization() {
        if (defaultValue == null) {
            return String.format("this.%s = %s;", fieldName, fieldName);
        }

        return String.format("this.%s = %s;", fieldName, defaultValue);
    }
}
