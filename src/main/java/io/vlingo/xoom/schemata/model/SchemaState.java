// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.schemata.model.Id.SchemaId;

public class SchemaState {
  public final SchemaId schemaId;
  public final Category category;
  public final Scope scope;
  public final String description;
  public final String name;

  public static SchemaState from(SchemaId schemaId) {
    return new SchemaState(schemaId);
  }

  public static SchemaState from(final SchemaId schemaId, final Category category, final Scope scope, final String name, final String description) {
    return new SchemaState(schemaId, category, scope, name, description);
  }

  public SchemaState defineWith(final Category category, final Scope scope, final String name, final String description) {
    return new SchemaState(this.schemaId, category, scope, name, description);
  }

  public SchemaState withCategory(final Category category) {
    return new SchemaState(this.schemaId, category, this.scope, this.name, this.description);
  }

  public SchemaState withScope(final Scope scope) {
    return new SchemaState(this.schemaId, this.category, scope, this.name, this.description);
  }

  public SchemaState withDescription(final String description) {
    return new SchemaState(this.schemaId, this.category, this.scope, this.name, description);
  }

  public SchemaState withName(final String name) {
    return new SchemaState(this.schemaId, this.category, this.scope, name, this.description);
  }

  public SchemaState redefineWith(final Category category, final Scope scope, final String name, final String description) {
    return new SchemaState(this.schemaId, category, scope, name, description);
  }

  @Override
  public int hashCode() {
    return 31 * this.schemaId.value.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    } else if (this == other) {
      return true;
    }

    final SchemaState otherState = (SchemaState) other;

    return this.schemaId == otherState.schemaId;
  }

  @Override
  public String toString() {
    return "SchemaState[schemaId=" + schemaId.value +
            " category=" + category.name() +
            " scope=" + scope.name() +
            " name=" + name +
            " description=" + description + "]";
  }

  private SchemaState(SchemaId schemaId) {
    this(schemaId, Category.Unknown, Scope.Private, "", "");
  }

  private SchemaState(final SchemaId schemaId, final Category category, final Scope scope, final String name, final String description) {
    this.schemaId = schemaId;
    this.category = category;
    this.scope = scope;
    this.name = name;
    this.description = description;
  }
}
