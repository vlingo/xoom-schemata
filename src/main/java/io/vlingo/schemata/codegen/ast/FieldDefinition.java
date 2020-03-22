package io.vlingo.schemata.codegen.ast;

import java.util.Objects;
import java.util.Optional;

import io.vlingo.schemata.codegen.ast.types.Type;
import io.vlingo.schemata.codegen.ast.values.NullValue;
import io.vlingo.schemata.codegen.ast.values.Value;
import io.vlingo.schemata.model.SchemaVersion;

@SuppressWarnings("rawtypes")
public class FieldDefinition implements Node {
    public final Type type;
    public final Optional<SchemaVersion.Version> version;
    public final String name;
    public final Optional<Value> defaultValue;

    public FieldDefinition(Type type, Optional<SchemaVersion.Version> version, String name, Optional<Value> defaultValue) {
        this.type = type;
        this.version = version;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldDefinition that = (FieldDefinition) o;
        return Objects.equals(type, that.type) &&
          Objects.equals(version, that.version) &&
          Objects.equals(name, that.name) &&
          Objects.equals(defaultValue, that.defaultValue);
    }

    public boolean hasDefaultValue() {
        return defaultValue.isPresent() && !defaultValue.get().equals(NullValue.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, version, name, defaultValue);
    }

    @Override
    public String name() {
        return name;
    }
}
