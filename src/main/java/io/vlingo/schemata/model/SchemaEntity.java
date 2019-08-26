// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.Collections;
import java.util.List;

import io.vlingo.common.Completes;
import io.vlingo.common.Tuple2;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.model.Events.SchemaCategorized;
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRenamed;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.symbio.Source;

public class SchemaEntity extends ObjectEntity<SchemaState> implements Schema {
  private SchemaState state;

  public SchemaEntity(final SchemaId schemaId) {
    this.state = SchemaState.from(schemaId);
  }

  @Override
  public Completes<SchemaState> defineWith(final Category category, final String name, final String description) {
    return apply(
      this.state.defineWith(category, name, description),
      SchemaDefined.with(state.schemaId, category, name, description),
      () -> this.state);
  }

  @Override
  public Completes<SchemaState> categorizeAs(final Category category) {
    return apply(this.state.withCategory(category), SchemaCategorized.with(state.schemaId, category), () -> this.state);
  }

  @Override
  public Completes<SchemaState> describeAs(String description) {
    return apply(this.state.withDescription(description), SchemaDescribed.with(state.schemaId, description), () -> this.state);
  }

  @Override
  public Completes<SchemaState> renameTo(String name) {
    return apply(this.state.withName(name), SchemaRenamed.with(state.schemaId, name), () -> this.state);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Tuple2<SchemaState, List<Source<DomainEvent>>> whenNewState() {
    return state.isIdentified() ? null : Tuple2.from(state, Collections.emptyList());
  }

  @Override
  protected String id() {
    return String.valueOf(state.persistenceId());
  }

  @Override
  protected void persistentObject(final SchemaState persistentObject) {
    this.state = persistentObject;
  }

  @Override
  protected Class<SchemaState> persistentObjectType() {
    return SchemaState.class;
  }
}
