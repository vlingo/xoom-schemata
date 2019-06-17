// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.backends.java.values;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JavaTypeDictionary {
  private JavaTypeDictionary() {

  }

  private final static Map<String, Class<?>> MAP = Collections.unmodifiableMap(new HashMap<String, Class<?>>() {
    private static final long serialVersionUID = 1L;

    {
      put("boolean", Boolean.class);
      put("byte", Byte.class);
      put("char", Character.class);
      put("double", Double.class);
      put("float", Float.class);
      put("int", Integer.class);
      put("long", Long.class);
      put("short", Short.class);
      put("string", String.class);
    }
  });

  public static Class<?> typeOf(final String typeName) {
    return MAP.get(typeName);
  }
}
