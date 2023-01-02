// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.sourcing.EventSourced;
import io.vlingo.xoom.schemata.model.Events.*;
import io.vlingo.xoom.schemata.model.Id.SchemaId;

public class SchemaEntity extends EventSourced implements Schema {
  private SchemaState state;

  public SchemaEntity(final SchemaId schemaId) {
    super(schemaId.value);
    this.state = SchemaState.from(schemaId);
  }

  @Override
  public Completes<SchemaState> defineWith(final Category category, final Scope scope, final String name, final String description) {
    return apply(SchemaDefined.with(this.state.schemaId, category, scope, name, description), () -> state);
  }

  @Override
  public Completes<SchemaState> categorizeAs(final Category category) {
    return apply(SchemaCategorized.with(state.schemaId, category), () -> state);
  }

  @Override
  public Completes<SchemaState> scopeAs(final Scope scope) {
    return apply(SchemaScoped.with(this.state.schemaId, scope), () -> state);
  }

  @Override
  public Completes<SchemaState> describeAs(String description) {
    return apply(SchemaDescribed.with(this.state.schemaId, description), () -> state);
  }

  @Override
  public Completes<SchemaState> redefineWith(final Category category, final Scope scope, final String name, final String description) {
    return apply(SchemaRedefined.with(this.state.schemaId, category, scope, name, description), () -> state);
  }

  @Override
  public Completes<SchemaState> renameTo(String name) {
    return apply(SchemaRenamed.with(this.state.schemaId, name), () -> state);
  }

  //==============================
  // Internal implementation
  //==============================

  private void applySchemaDefined(SchemaDefined event) {
    this.state = state.defineWith(Category.valueOf(event.category), Scope.valueOf(event.scope), event.name, event.description);
  }

  private void applySchemaCategorized(SchemaCategorized event) {
    this.state = state.withCategory(Category.valueOf(event.category));
  }

  private void applySchemaScoped(SchemaScoped event) {
    this.state = state.withScope(Scope.valueOf(event.scope));
  }

  private void applySchemaDescribed(SchemaDescribed event) {
    this.state = state.withDescription(event.description);
  }

  private void applySchemaRedefined(SchemaRedefined event) {
    this.state = state.redefineWith(Category.valueOf(event.category), Scope.valueOf(event.scope), event.name, event.description);
  }

  private void applySchemaRenamed(SchemaRenamed event) {
    this.state = state.withName(event.name);
  }

  static {
    registerConsumer(SchemaEntity.class, SchemaDefined.class, SchemaEntity::applySchemaDefined);
    registerConsumer(SchemaEntity.class, SchemaCategorized.class, SchemaEntity::applySchemaCategorized);
    registerConsumer(SchemaEntity.class, SchemaScoped.class, SchemaEntity::applySchemaScoped);
    registerConsumer(SchemaEntity.class, SchemaDescribed.class, SchemaEntity::applySchemaDescribed);
    registerConsumer(SchemaEntity.class, SchemaRedefined.class, SchemaEntity::applySchemaRedefined);
    registerConsumer(SchemaEntity.class, SchemaRenamed.class, SchemaEntity::applySchemaRenamed);
  }
}
