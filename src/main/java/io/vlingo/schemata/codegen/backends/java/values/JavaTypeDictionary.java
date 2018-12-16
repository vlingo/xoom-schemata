package io.vlingo.schemata.codegen.backends.java.values;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JavaTypeDictionary {
    private JavaTypeDictionary() {

    }

    private final static Map<String, Class<?>> MAP = Collections.unmodifiableMap(new HashMap<String, Class<?>>() {{
        put("boolean", Boolean.class);
        put("byte", Byte.class);
        put("char", Character.class);
        put("double", Double.class);
        put("float", Float.class);
        put("int", Integer.class);
        put("long", Long.class);
        put("short", Short.class);
        put("string", String.class);
    }});

    public static Class<?> typeOf(String typeName) {
        return MAP.get(typeName);
    }
}
