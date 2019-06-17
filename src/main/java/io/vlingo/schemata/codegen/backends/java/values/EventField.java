// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.backends.java.values;

import java.util.Optional;

import io.vlingo.common.Tuple2;

public abstract class EventField {
  protected final String ownerClass;
  protected final String typeName;
  protected final String fieldName;

  protected EventField(final String ownerClass, final String typeName, final String fieldName) {
    this.ownerClass = ownerClass;
    this.typeName = typeName;
    this.fieldName = fieldName;
  }

  public abstract Tuple2<Class<?>, String> fieldDefinition();

  public abstract Optional<Tuple2<Class<?>, String>> parameterDefinition();

  public abstract String initialization();
}
