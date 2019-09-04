package io.vlingo.schemata.codegen.ast;

import io.vlingo.schemata.codegen.ast.types.Type;
import io.vlingo.schemata.codegen.ast.values.Value;
import io.vlingo.schemata.model.SchemaVersion;

import java.util.Optional;

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
}
