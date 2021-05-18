package io.vlingo.xoom.schemata.codegen;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum TypeMap {

    java(ofMapEntries(
            "boolean", "boolean",
            "byte", "byte",
            "char", "char",
            "short", "short",
            "int", "int",
            "long", "long",
            "float", "float",
            "double", "double",
            "string", "String",
            "type", "String",
            "version", "int",
            "timestamp", "long"
    )),
    csharp(ofMapEntries(
            "boolean", "bool",
            "byte", "byte",
            "char", "char",
            "short", "short",
            "int", "int",
            "long", "long",
            "float", "float",
            "double", "double",
            "type", "string",
            "version", "int",
            "timestamp", "long"
    ));

    final Map<String, String> types;

    TypeMap(Map<String, String> types) {
        this.types = Collections.unmodifiableMap(types);
    }

    public String typeOf(String type){
        return types.getOrDefault(type, type);
    }

    private static Map<String, String> ofMapEntries(String ... entries) {
        if (entries.length % 2 != 0){
            throw new IllegalArgumentException("parameters must even");
        }
        Map<String, String> result = new HashMap<>();
        for (int i=0; i<entries.length-1; i+=2){
            String key = entries[i];
            String val = entries[i+1];
            result.put(key, val);
        }
        return result;
    }
}
