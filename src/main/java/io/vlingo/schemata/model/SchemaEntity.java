// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Events.SchemaCategorized;
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRedefined;
import io.vlingo.schemata.model.Events.SchemaRenamed;
import io.vlingo.schemata.model.Events.SchemaScoped;
import io.vlingo.schemata.model.Id.SchemaId;

public class SchemaEntity extends EventSourced implements Schema {
  private SchemaState state;

  public SchemaEntity(final SchemaId schemaId) {
    super(schemaId.value);
    this.state = SchemaState.from(schemaId);
  }

  @Override
  public Completes<SchemaState> defineWith(final Category category, final Scope scope, final String name, final String description) {
    return apply(SchemaDefined.with(state.schemaId, category, scope, name, description), () -> this.state);
  }

  @Override
  public Completes<SchemaState> categorizeAs(final Category category) {
    return apply(SchemaCategorized.with(state.schemaId, category), () -> this.state);
  }

  @Override
  public Completes<SchemaState> scopeAs(final Scope scope) {
    return apply(SchemaScoped.with(state.schemaId, scope), () -> this.state);
  }

  @Override
  public Completes<SchemaState> describeAs(String description) {
    return apply(SchemaDescribed.with(state.schemaId, description), () -> this.state);
  }

  @Override
  public Completes<SchemaState> redefineWith(final Category category, final Scope scope, final String name, final String description) {
    return apply(SchemaRedefined.with(state.schemaId, category, scope, name, description), () -> this.state);
  }

  @Override
  public Completes<SchemaState> renameTo(String name) {
    return apply(SchemaRenamed.with(state.schemaId, name), () -> this.state);
  }

  //==============================
  // Internal implementation
  //==============================

  private void applySchemaDefined(final SchemaDefined event) {
    this.state = state.defineWith(Category.valueOf(event.category), Scope.valueOf(event.scope), event.name, event.description);
  }

  private void applySchemaCategorized(final SchemaCategorized event) {
    this.state = state.withCategory(Category.valueOf(event.category));
  }

  private void applySchemaScoped(final SchemaScoped event) {
    this.state = state.withScope(Scope.valueOf(event.scope));
  }

  private void applySchemaDescribed(final SchemaDescribed event) {
    this.state = state.withDescription(event.description);
  }

  private void applySchemaRedefined(final SchemaRedefined event) {
    this.state = state.redefineWith(Category.valueOf(event.category), Scope.valueOf(event.scope), event.name, event.description);
  }

  private void applySchemaRenamed(final SchemaRenamed event) {
    this.state = this.state.withName(event.name);
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
