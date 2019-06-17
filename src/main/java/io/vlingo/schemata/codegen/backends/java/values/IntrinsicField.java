// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.backends.java.values;

import java.util.Optional;

import io.vlingo.common.Tuple2;

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

  public static IntrinsicField of(final String ownerClass, final String typeName, final String fieldName) {
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
