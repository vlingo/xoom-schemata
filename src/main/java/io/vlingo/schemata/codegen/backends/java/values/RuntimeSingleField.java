// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.backends.java.values;

import java.util.Optional;

import io.vlingo.common.Tuple2;

public final class RuntimeSingleField extends EventField {
  private final String defaultValue;

  protected RuntimeSingleField(final String ownerClass, final String typeName, final String fieldName, final String defaultValue) {
    super(ownerClass, typeName, fieldName);
    this.defaultValue = defaultValue;
  }

  public static EventField of(final String ownerClass, final String typeName, final String fieldName, final String defaultValue) {
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
