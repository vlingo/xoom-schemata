package io.vlingo.schemata.codegen.backends.java.values;

import io.vlingo.common.Tuple2;

import java.util.Optional;

public abstract class EventField {
    protected final String ownerClass;
    protected final String typeName;
    protected final String fieldName;

    protected EventField(String ownerClass, String typeName, String fieldName) {
        this.ownerClass = ownerClass;
        this.typeName = typeName;
        this.fieldName = fieldName;
    }

    public abstract Tuple2<Class<?>, String> fieldDefinition();
    public abstract Optional<Tuple2<Class<?>, String>> parameterDefinition();
    public abstract String initialization();
}
