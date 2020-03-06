// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.model.Events.SchemaCategorized;
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRedefined;
import io.vlingo.schemata.model.Events.SchemaRenamed;
import io.vlingo.schemata.model.Events.SchemaScoped;
import io.vlingo.schemata.model.Id.SchemaId;

public class SchemaEntity extends ObjectEntity<SchemaState> implements Schema {
  private SchemaState state;

  public SchemaEntity(final SchemaId schemaId) {
    this.state = SchemaState.from(schemaId);
  }

  @Override
  public Completes<SchemaState> defineWith(final Category category, final Scope scope, final String name, final String description) {
    return apply(
      this.state.defineWith(category, scope, name, description),
      SchemaDefined.with(state.schemaId, category, scope, name, description),
      () -> this.state);
  }

  @Override
  public Completes<SchemaState> categorizeAs(final Category category) {
    return apply(this.state.withCategory(category), SchemaCategorized.with(state.schemaId, category), () -> this.state);
  }

  @Override
  public Completes<SchemaState> scopeAs(final Scope scope) {
    return apply(this.state.withScope(scope), SchemaScoped.with(state.schemaId, scope), () -> this.state);
  }

  @Override
  public Completes<SchemaState> describeAs(String description) {
    return apply(this.state.withDescription(description), SchemaDescribed.with(state.schemaId, description), () -> this.state);
  }

  @Override
  public Completes<SchemaState> redefineWith(final Category category, final Scope scope, final String name, final String description) {
    return apply(
            this.state.redefineWith(category, scope, name, description),
            SchemaRedefined.with(state.schemaId, category, scope, name, description),
            () -> this.state);
  }

  @Override
  public Completes<SchemaState> renameTo(String name) {
    return apply(this.state.withName(name), SchemaRenamed.with(state.schemaId, name), () -> this.state);
  }

  @Override
  protected String id() {
    return String.valueOf(state.persistenceId());
  }

  @Override
  protected SchemaState stateObject() {
    return state;
  }

  @Override
  protected void stateObject(final SchemaState stateObject) {
    this.state = stateObject;
  }

  @Override
  protected Class<SchemaState> stateObjectType() {
    return SchemaState.class;
  }

  @Override
  public void applyRelocationSnapshot(String snapshot) {
    stateObject(SchemaState.from(SchemaId.existing(snapshot)));
  }
}
